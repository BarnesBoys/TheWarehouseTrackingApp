package com.example.brooke.thewarehousetrackingapp;

class TickBool {
        private boolean bool = false;
        private ChangeListener listener;

         TickBool(){
            bool = false;
        }

        boolean getBool() {
            return bool;
        }

        void setBool(boolean b) {
            bool = b;
            if (listener != null) listener.onChange();
        }

        ChangeListener getListener() {
            return listener;
        }

        void setListener(ChangeListener listener) {
            this.listener = listener;
        }

        interface ChangeListener {
            void onChange();
        }
}
