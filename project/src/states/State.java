package states;

import context.Context;

public enum State {
    FOLLOWER() {
        @Override
        public State execute(Context context) {
            return Follower.execute(context);
        }
    },
    CANDIDATE() {
        @Override
        public State execute(Context context) {
            return Candidate.execute(context);
        }
    },
    LEADER() {
        @Override
        public State execute(Context context) {
            return Leader.execute(context);
        }
    },
    HALT() {
        @Override
        public State execute(Context context) {
            return HALT;
        }
    };
    public abstract State execute(Context context);
}
