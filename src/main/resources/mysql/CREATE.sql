-- -----------------------------------------------------
-- Player Table
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `[PLAYER_DB]` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `playername` VARCHAR(16) NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Inventory Table
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `[INVENTORY_DB]` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `playerID` INT NOT NULL ,
  `content` BLOB NULL ,
  `armor` BLOB NULL ,
  `server` CHAR(32) NULL ,
  `min_health` DOUBLE NOT NULL,
  `max_health` DOUBLE NOT NULL, 
  `food` INT NOT NULL,
  PRIMARY KEY (`id`, `playerID`) ,
  INDEX `fk_inventories_player_idx` (`playerID` ASC) ,
  UNIQUE INDEX `adf` USING BTREE (`playerID` ASC, `server` ASC) ,
  CONSTRAINT `fk_inventories_player`
    FOREIGN KEY (`playerID` )
    REFERENCES `[PLAYER_DB]` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Enderchest Table
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `[ENDERCHEST_DB]` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerID` int(11) NOT NULL,
  `content` blob,
  `server` char(32) DEFAULT NULL,
  PRIMARY KEY (`id`,`playerID`),
  UNIQUE KEY `adf` (`playerID`,`server`) USING BTREE,
  KEY `fk_enderchest_player_idx` (`playerID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1

-- -----------------------------------------------------
-- | UPGRADING EXISTING DATABASES:                     |
-- | These statements should update your database to   |
-- | fit major changes.                                |
-- | Remember: Replace the placeholder in [] and       |
-- | Remove the '--' and use your MySQL console or MySQL |
-- | manager such as phpmyadmin.                       |
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Add stats to Playerdatabase: max, min-HP, food
-- -----------------------------------------------------

-- ALTER TABLE `[INVENTORY_DB]` ADD `min_health` DOUBLE NOT NULL DEFAULT 20;
-- ALTER TABLE `[INVENTORY_DB]` ADD `max_health` DOUBLE NOT NULL DEFAULT 20;
-- ALTER TABLE `[INVENTORY_DB]` ADD `food` INT NOT NULL DEFAULT 20;
