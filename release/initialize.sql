DROP TABLE IF EXISTS `biased_preference_model`.`trait_freq_history`;

CREATE TABLE `biased_preference_model`.`trait_freq_history`(
id INT(11) NOT NULL AUTO_INCREMENT,
timestep INT(11) NOT NULL,
trait_kind INT(11) NOT NULL,
freq BIGINT NOT NULL,
PRIMARY KEY (id)
);