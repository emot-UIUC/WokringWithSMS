package org.emot.libcontrol;


class AnimationFactory {
    static Runnable getRunnable(Emotions which) {
        switch (which) {
            case RESET:
                return new ResetAnimation();
            case ANGRY:
                return new AngryAnimation();
            case HAPPY:
                return new HappyAnimation();
            case SAD:
                return new SadAnimation();
            case FEAR:
                return new FearAnimation();
            case DISGUST:
                return new DisgustAnimation();
            case SURPRISE:
                return new SurpriseAnimation();
            default:
                return null;
        }
    }

    private static class ResetAnimation implements Runnable {

        @Override
        public void run() {
            EmotControl.setLed(Leds.LEFT, LedColors.BLACK);
            EmotControl.setLed(Leds.RIGHT, LedColors.BLACK);
            EmotControl.setArm(Arms.LEFT, ArmActions.STABLE);
            EmotControl.setArm(Arms.RIGHT, ArmActions.STABLE);
        }
    }

    private static class AngryAnimation implements Runnable {

        @Override
        public void run() {
            try {
                EmotControl.setLed(Leds.LEFT, LedColors.RED);
                EmotControl.setLed(Leds.RIGHT, LedColors.RED);
                EmotControl.setArm(Arms.LEFT, ArmActions.UP);
                EmotControl.setArm(Arms.RIGHT, ArmActions.UP);
                Thread.sleep(2000);
                EmotControl.setLed(Leds.LEFT, LedColors.RED);
                EmotControl.setLed(Leds.RIGHT, LedColors.RED);
                EmotControl.setArm(Arms.LEFT, ArmActions.DOWN);
                EmotControl.setArm(Arms.RIGHT, ArmActions.DOWN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class HappyAnimation implements Runnable {

        @Override
        public void run() {
            try {
                EmotControl.setLed(Leds.LEFT, LedColors.YELLOW);
                EmotControl.setLed(Leds.RIGHT, LedColors.YELLOW);
                EmotControl.setArm(Arms.LEFT, ArmActions.DOWN);
                EmotControl.setArm(Arms.RIGHT, ArmActions.DOWN);
                Thread.sleep(2000);
                EmotControl.setLed(Leds.LEFT, LedColors.RED);
                EmotControl.setLed(Leds.RIGHT, LedColors.RED);
                EmotControl.setArm(Arms.LEFT, ArmActions.UP);
                EmotControl.setArm(Arms.RIGHT, ArmActions.UP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class SadAnimation implements Runnable {

        @Override
        public void run() {
            // @todo
        }
    }

    private static class FearAnimation implements Runnable {

        @Override
        public void run() {
            // @todo
        }
    }

    private static class DisgustAnimation implements Runnable {

        @Override
        public void run() {
            // @todo
        }
    }

    private static class SurpriseAnimation implements Runnable {

        @Override
        public void run() {
            // @todo
        }
    }
}
