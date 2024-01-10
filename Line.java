public class Line<T extends Thesis>{
    private String content;
    private Thesis.State state;
    Thread[] writers = new Thread[5];

    public Thread createWriterThread(Runnable r) {
        return new Thread(r);
    }

    public Line(T ghost, String title){
        content = title;
        state = Thesis.State.INTRO;

        for(int i = 0; i < 5; i++){
            int finalI = i;
            writers[i] = createWriterThread(() ->{
                Thesis.State targetState = Thesis.State.values()[finalI];
                synchronized (ghost){
                    while(state != targetState){
                        try {
                            ghost.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    switch(targetState){
                        case INTRO -> content += ghost.intro();
                        case SETUP -> content += ghost.setup();
                        case EXPERIMENTS -> content += ghost.experiments();
                        case CONCLUSION -> content += ghost.conclusion();
                        case REFS -> content += ghost.refs();
                    }

                    state = Thesis.State.values()[finalI +1];
                    ghost.notifyAll();
                }
            });
        }

        for(int i = 0; i < 5; i++){
            writers[i].start();
        }

        if(state == Thesis.State.FINISHED){
            for(int i = 0; i < 5; i++){
                writers[i].interrupt();
            }
        }
    }

    public String result(){
        return content;
    }

    public static void main(String[] args){
        class MyThesis implements Thesis{

            @Override
            public String intro() {
                return args[1];
            }

            @Override
            public String setup() {
                return args[2];
            }

            @Override
            public String experiments() {
                return args[3];
            }

            @Override
            public String conclusion() {
                return args[4];
            }

            @Override
            public String refs() {
                return args[5];
            }
        }

        MyThesis thesis = new MyThesis();
        Line<MyThesis> myThesis = new Line<>(thesis, args[0]);
        for (Thread writer : myThesis.writers){
            try {
                writer.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(myThesis.result());
    }
}
