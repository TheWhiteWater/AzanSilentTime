package nz.co.redice.demoservice.view.presentation;

import androidx.annotation.NonNull;

public enum Category {

    HOME {
        @NonNull
        @Override
        public String toString() {
            return "Home";
        }
        public String getTag() {return "";}
    },
    SETTINGS {
        @NonNull
        @Override
        public String toString() {
            return "Settings";
        }
        public String getTag() {return "settings";}

    },
    MAP {
        @NonNull
        @Override
        public String toString() {
            return "Map";
        }
        public String getTag() {return "map";}

    }


}
