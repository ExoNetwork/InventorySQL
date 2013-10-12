-- -----------------------------------------------------
-- Player Table
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `player` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `playername` VARCHAR(16) NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Inventory Table
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `inventory` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `playerID` INT NOT NULL ,
  `content` BLOB NULL ,
  `armor` BLOB NULL ,
  `server` CHAR(32) NULL ,
  PRIMARY KEY (`id`, `playerID`) ,
  INDEX `fk_inventories_player_idx` (`playerID` ASC) ,
  UNIQUE INDEX `adf` USING BTREE (`playerID` ASC, `server` ASC) ,
  CONSTRAINT `fk_inventories_player`
    FOREIGN KEY (`playerID` )
    REFERENCES `player` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;