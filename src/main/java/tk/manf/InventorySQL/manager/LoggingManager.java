/**
 * Copyright (c) 2013 Exo-Network
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 * 
 * manf                   info@manf.tk
 */

package tk.manf.InventorySQL.manager;

import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LoggingManager {
    private static final Logger logger = Logger.getLogger("Minecraft");
    @Setter
    private String prefix;
    @Setter
    private int level;

    private LoggingManager() {
    }

    public void d(String msg) {
        log(Level.DEBUG, msg);
    }

    public void logDeveloperMessage(String developer, DeveloperMessages msg) {
        log(Level.DEVELOPER_MESSAGE, "[Developer Message from " + developer + "] " + msg.getMessage());
    }

    public void log(Exception ex) {
        if (this.level > Level.ERROR) {
            logger.log(java.util.logging.Level.INFO, null, ex);
        }
    }

    public void log(int level, String msg) {
        log(level, java.util.logging.Level.INFO, msg);
    }

    private void log(int level, java.util.logging.Level l, String msg) {
        if (this.level > level) {
            logger.log(l, "[{0}][DEBUG] {1}", new Object[]{prefix, msg});
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static class Level {
        public static final int DEBUG = 999;
        //public static final int NO = 899;
        //public static final int BODY = 799;
        //public static final int CARES = 699;
        //public static final int THE = 599;
        //public static final int DOCTOR = 499;
        //public static final int IS = 399;
        //pulic static final int JUST = 299;
        //public static final int EPIC = 199;
        public static final int DEVELOPER_MESSAGE = 99;
        public static final int ERROR = 0;
    }

    @AllArgsConstructor
    public static enum DeveloperMessages {
        //#SUPPORT_DEVELOPERS
        //Just remind people to be supportive.
        METRICS_OFF("You turned metrics off :(. Metrics is a good way to support Developers! ( You should also consider donating :) )"),
        METRICS_LOADED("Thanks for supporting us and using Metrics :)"),
        DEPRECATED_CLASS("Sorry, but the loaded class is deprecated");
        @Getter
        private String message;
    }

    static class Herobrine {
        //Darkness
    }
    @Getter
    private static final LoggingManager instance = new LoggingManager();
}
