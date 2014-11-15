/*Grouping処理が多いため、Groupingで使うカラムに複合インデックスを貼った*/
ALTER TABLE `biased_preference_model`.`trait_freq_history` ADD INDEX grouping_top_n (sim_num, trait_kind);