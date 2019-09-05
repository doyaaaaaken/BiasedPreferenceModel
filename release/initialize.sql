DROP TABLE IF EXISTS `biased_preference_model`.`trait_freq_history`;

CREATE TABLE `biased_preference_model`.`trait_freq_history`(
id INT(11) NOT NULL AUTO_INCREMENT,
timestep INT(11) NOT NULL,
trait_kind INT(11) NOT NULL,
freq BIGINT NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE `biased_preference_model`.`preference_history_for_one_trait`(
id INT(11) NOT NULL AUTO_INCREMENT,
timestep INT(11) NOT NULL,
trait_kind INT(11) NOT NULL,
agent_id INT(11) NOT NULL,
preference DOUBLE NOT NULL,
PRIMARY KEY (id)
);