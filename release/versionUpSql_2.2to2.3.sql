ALTER TABLE `biased_preference_model`.`trait_freq_history` ADD sim_num INT(11) NOT NULL AFTER id;

ALTER TABLE `biased_preference_model`.`preference_history_for_one_trait` ADD sim_num INT(11) NOT NULL AFTER id;
