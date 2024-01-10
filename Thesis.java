public interface Thesis {
    String intro();
    String setup();
    String experiments();
    String conclusion();
    String refs();
    enum State{
        INTRO, SETUP, EXPERIMENTS, CONCLUSION, REFS, FINISHED;
    }
}
