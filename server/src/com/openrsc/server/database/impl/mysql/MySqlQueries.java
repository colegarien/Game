package com.openrsc.server.database.impl.mysql;

import com.openrsc.server.Server;
import com.openrsc.server.external.SkillDef;

public class MySqlQueries {
	public final String PREFIX;

	public String updateExperience, updateStats, playerExp, playerCurExp;
	public final String createPlayer, recentlyRegistered, initStats, initExp;
	public final String save_AddFriends, save_DeleteFriends, save_AddIgnored, save_DeleteIgnored;
	public final String playerExists, playerData, playerInvItems, playerEquipped, playerBankItems, playerBankPresets;
	public final String playerFriends, playerIgnored, playerQuests, playerAchievements, playerCache;
	public final String save_ItemCreate, save_ItemUpdate, save_ItemPurge; //itemstatuses, must be inserted before adding entry on bank, equipment, inventory
	public final String save_DeleteBank, save_DeleteBankPresets, save_BankAdd, save_BankRemove, save_BankPresetAdd, save_BankPresetRemove;
	public final String save_DeleteInv, save_InventoryAdd, save_InventoryRemove, save_DeleteEquip, save_EquipmentAdd, save_EquipmentRemove, save_UpdateBasicInfo;
	public final String save_DeleteQuests, save_DeleteAchievements, save_DeleteCache, save_AddCache, save_AddQuest, save_AddAchievement;
	public final String save_Password, save_PreviousPasswords, previousPassword, achievements, rewards, tasks;
	public final String playerLoginData, fetchLoginIp, fetchLinkedPlayers, playerPendingRecovery, playerRecoveryInfo, playerRecoveryAttempt, userToId, initializeOnlineUsers;
	public final String npcKillSelectAll, npcKillSelect, npcKillInsert, npcKillUpdate, playerLastRecoveryTryId;
	public final String dropLogSelect, dropLogInsert, dropLogUpdate, npcDefs, npcDrops, itemDefs, banPlayer, unbanPlayer;
	public final String addNpcSpawn, removeNpcSpawn, addObjectSpawn, removeObjectSpawn, addItemSpawn, removeItemSpawn;
	public final String clans, clanMembers, newClan, saveClanMember, deleteClan, deleteClanMembers, updateClan, updateClanMember;

	private final Server server;

	public final Server getServer() {
		return server;
	}

	public MySqlQueries(final Server server) {
		this.server = server;
		PREFIX = getServer().getConfig().MYSQL_TABLE_PREFIX;

		updateExperience = "UPDATE `" + PREFIX + "experience` SET ";
		updateStats = "UPDATE `" + PREFIX + "curstats` SET ";
		playerExp = "SELECT ";
		playerCurExp = "SELECT ";

		for (SkillDef skill : getServer().getConstants().getSkills().skills) {
			updateExperience = updateExperience + "`exp_" + skill.getShortName().toLowerCase() + "`=?, ";
			updateStats = updateStats + "`cur_" + skill.getShortName().toLowerCase() + "`=?, ";
			playerExp = playerExp + "`exp_" + skill.getShortName().toLowerCase() + "`, ";
			playerCurExp = playerCurExp + "`cur_" + skill.getShortName().toLowerCase() + "`, ";
		}

		updateExperience = updateExperience.substring(0, updateExperience.length() - 2) + " ";
		updateStats = updateStats.substring(0, updateStats.length() - 2) + " ";
		playerExp = playerExp.substring(0, playerExp.length() - 2) + " ";
		playerCurExp = playerCurExp.substring(0, playerCurExp.length() - 2) + " ";

		updateExperience = updateExperience + "WHERE `playerID`=?";
		updateStats = updateStats + "WHERE `playerID`=?";
		playerExp = playerExp + "FROM `" + PREFIX + "experience` WHERE `playerID`=?";
		playerCurExp = playerCurExp + "FROM `" + PREFIX + "curstats` WHERE `playerID`=?";

		createPlayer = "INSERT INTO `" + PREFIX + "players` (`username`, `email`, `pass`, `creation_date`, `creation_ip`) VALUES (?, ?, ?, ?, ?)";
		recentlyRegistered = "SELECT 1 FROM `" + PREFIX + "players` WHERE `creation_ip`=?" +
			" AND `creation_date` > ?";
		initStats = "INSERT INTO `" + PREFIX + "curstats` (`playerID`) VALUES (?)";
		initExp = "INSERT INTO `" + PREFIX + "experience` (`playerID`) VALUES (?)";

		save_AddFriends = "INSERT INTO `" + PREFIX + "friends`(`playerID`, `friend`, `friendName`) VALUES(?, ?, ?)";
		save_DeleteFriends = "DELETE FROM `" + PREFIX + "friends` WHERE `playerID` = ?";
		save_AddIgnored = "INSERT INTO `" + PREFIX + "ignores`(`playerID`, `ignore`) VALUES(?, ?)";
		save_DeleteIgnored = "DELETE FROM `" + PREFIX + "ignores` WHERE `playerID` = ?";
		playerExists = "SELECT 1 FROM `" + PREFIX + "players` WHERE `id` = ?";
		playerData = "SELECT `id`, `group_id`, "
			+ "`combatstyle`, `login_date`, `login_ip`, `x`, `y`, `fatigue`,  `kills`,"
			+ "`deaths`, `kills2`, `iron_man`, `iron_man_restriction`,`hc_ironman_death`, `quest_points`, `block_chat`, `block_private`,"
			+ "`block_trade`, `block_duel`, `cameraauto`,"
			+ "`onemouse`, `soundoff`, `haircolour`, `topcolour`,"
			+ "`trousercolour`, `skincolour`, `headsprite`, `bodysprite`, `male`,"
			+ "`pass`, `salt`, `bank_size`, `combat`, `skill_total`, `muted` FROM `" + PREFIX + "players` WHERE `username`=?";
		playerInvItems = "SELECT i.*,i2.* FROM `" + PREFIX + "invitems` i JOIN `" + PREFIX + "itemstatuses` i2 ON i.`itemID`=i2.`itemID` WHERE i.`playerID`=? ORDER BY `slot` ASC";
		playerEquipped = "SELECT i.`itemID`,i2.* FROM `" + PREFIX + "equipped` i JOIN `" + PREFIX + "itemstatuses` i2 ON i.`itemID`=i2.`itemID` WHERE i.`playerID`=?";
		playerBankItems = "SELECT i.`itemID`,i2.* FROM `" + PREFIX + "bank` i JOIN `" + PREFIX + "itemstatuses` i2 ON i.`itemID`=i2.`itemID` WHERE i.`playerID`=? ORDER BY `slot` ASC";
		playerBankPresets = "SELECT `slot`, `inventory`, `equipment` FROM `" + PREFIX + "bankpresets` WHERE `playerID`=?";
		playerFriends = "SELECT `friend` FROM `" + PREFIX + "friends` WHERE `playerID`=?";
		playerIgnored = "SELECT `ignore` FROM `" + PREFIX + "ignores` WHERE `playerID`=?";
		playerQuests = "SELECT `id`, `stage` FROM `" + PREFIX + "quests` WHERE `playerID`=?";
		playerAchievements = "SELECT `id`, `status` FROM `" + PREFIX + "achievement_status` WHERE `playerID`=?";
		playerCache = "SELECT `type`, `key`, `value` FROM `" + PREFIX + "player_cache` WHERE `playerID`=?";
		save_DeleteBank = "DELETE i,i2 FROM `" + PREFIX + "bank` i JOIN `" + PREFIX + "itemstatuses` i2 ON i.`itemID`=i2.`itemID` WHERE i.`playerID`=?";
		save_DeleteBankPresets = "DELETE FROM `" + PREFIX + "bankpresets` WHERE `playerID`=? AND `slot`=?";
		save_ItemCreate = "INSERT INTO `" + PREFIX + "itemstatuses`(`catalogID`, `amount`, `noted`, `durability`) VALUES(?, ?, ?, ?)";
		save_ItemPurge = "DELETE FROM `" + PREFIX + "itemstatuses` WHERE `itemID`=?";
		save_ItemUpdate = "UPDATE `" + PREFIX + "itemstatuses` SET `amount`=?, `noted`=?, `durability`=? WHERE `itemID`=?";
		save_BankAdd = "INSERT INTO `" + PREFIX + "bank`(`playerID`, `itemID`, `slot`) VALUES(?, ?, ?)";
		save_BankRemove = "DELETE FROM `" + PREFIX + "bank` WHERE `playerID`=? AND `itemID`=?";
		save_BankPresetRemove = "DELETE FROM `" + PREFIX + "bankpresets` WHERE `playerID`=? AND `slot`=?";
		save_BankPresetAdd = "INSERT INTO `" + PREFIX + "bankpresets`(`playerID`, `slot`, `inventory`, `equipment`) VALUES(?, ?, ?, ?)";
		save_DeleteInv = "DELETE FROM `" + PREFIX + "invitems` WHERE `playerID`=?";
		save_InventoryAdd = "INSERT INTO `" + PREFIX + "invitems`(`playerID`, `itemID`, `wielded`, `slot`) VALUES(?, ?, ?, ?)";
		save_InventoryRemove = "DELETE FROM `" + PREFIX + "invitems` WHERE `playerID`=? AND `itemID`=?";
		save_DeleteEquip = "DELETE FROM `" + PREFIX + "equipped` WHERE `playerID`=?";
		save_EquipmentAdd = "INSERT INTO `" + PREFIX + "equipped`(`playerID`, `itemID`) VALUES(?, ?)";
		save_EquipmentRemove = "DELETE FROM `" + PREFIX + "equipped` WHERE `playerID`=? AND `itemID`=?";
		save_UpdateBasicInfo = "UPDATE `" + PREFIX + "players` SET `combat`=?, skill_total=?, " +
			"`x`=?, `y`=?, `fatigue`=?, `kills`=?, `deaths`=?, `kills2`=?, `iron_man`=?, `iron_man_restriction`=?, " +
			"`hc_ironman_death`=?, `quest_points`=?, `haircolour`=?, `topcolour`=?, `trousercolour`=?, `skincolour`=?, " +
			"`headsprite`=?, `bodysprite`=?, `male`=?, `combatstyle`=?, `muted`=?, `bank_size`=?, `group_id`=?," +
			"`block_chat`=?, `block_private`=?, `block_trade`=?, `block_duel`=?, `cameraauto`=?, `onemouse`=?, `soundoff`=? WHERE `id`=?";
		save_DeleteQuests = "DELETE FROM `" + PREFIX + "quests` WHERE `playerID`=?";
		save_DeleteAchievements = "DELETE FROM `" + PREFIX + "achievement_status` WHERE `playerID`=?";
		save_DeleteCache = "DELETE FROM `" + PREFIX + "player_cache` WHERE `playerID`=?";
		save_AddQuest = "INSERT INTO `" + PREFIX + "quests` (`playerID`, `id`, `stage`) VALUES(?, ?, ?)";
		save_AddAchievement = "INSERT INTO `" + PREFIX + "achievement_status` (`playerID`, `id`, `status`) VALUES(?, ?, ?)";
		save_AddCache = "INSERT INTO `" + PREFIX + "player_cache` (`playerID`, `type`, `key`, `value`) VALUES(?,?,?,?)";
		save_Password = "UPDATE `" + PREFIX + "players` SET `pass`=? WHERE `id`=?";
		save_PreviousPasswords = "UPDATE `" + PREFIX + "player_recovery` SET `previous_pass`=?, `earlier_pass`=? WHERE `playerID`=?";
		previousPassword = "SELECT `previous_pass` FROM `" + PREFIX + "player_recovery` WHERE `playerID`=?";
		achievements = "SELECT `id`, `name`, `description`, `extra`, `added` FROM `" + PREFIX + "achievements` ORDER BY `id` ASC";
		rewards = "SELECT `item_id`, `amount`, `guaranteed`, `reward_type` FROM `" + PREFIX + "achievement_reward` WHERE `achievement_id` = ?";
		tasks = "SELECT `type`, `do_id`, `do_amount` FROM `" + PREFIX + "achievement_task` WHERE `achievement_id` = ?";
		playerLoginData = "SELECT `id`, `group_id`, `pass`, `salt`, `banned` FROM `" + PREFIX + "players` WHERE `username`=?";
		playerPendingRecovery = "SELECT `username`, `question1`, `answer1`, `question2`, `answer2`, " +
			"`question3`, `answer3`, `question4`, `answer4`, `question5`, `answer5`, `date_set`, " +
			"`ip_set` FROM `" + PREFIX + "player_change_recovery` WHERE `playerID`=?";
		playerRecoveryInfo = "SELECT * FROM " + PREFIX + "player_recovery WHERE playerID=?";
		playerRecoveryAttempt = "INSERT INTO `" + PREFIX + "recovery_attempts`(`playerID`, `username`, `time`, `ip`) VALUES(?, ?, ?, ?)";
		playerLastRecoveryTryId = "UPDATE `" + PREFIX + "players` SET `lastRecoveryTryId`=? WHERE `id`=?";
		userToId = "SELECT DISTINCT `id` FROM `" + PREFIX + "players` WHERE `username`=?";
		npcKillSelectAll = "SELECT * FROM `" + PREFIX + "npckills` WHERE playerID = ?";
		npcKillSelect = "SELECT * FROM `" + PREFIX + "npckills` WHERE npcID = ? AND playerID = ?";
		npcKillInsert = "INSERT INTO `" + PREFIX + "npckills`(killCount, npcID, playerID) VALUES (?, ?, ?)";
		npcKillUpdate = "UPDATE `" + PREFIX + "npckills` SET killCount = ? WHERE ID = ? AND npcID = ? AND playerID =?";
		dropLogSelect = "SELECT * FROM `" + PREFIX + "droplogs` WHERE itemID = ? AND playerID = ?";
		dropLogInsert = "INSERT INTO `" + PREFIX + "droplogs`(itemID, playerID, dropAmount, npcId) VALUES (?, ?, ?, ?)";
		dropLogUpdate = "UPDATE `" + PREFIX + "droplogs` SET dropAmount = ? WHERE itemID = ? AND playerID = ?";
		npcDefs = "SELECT `id`, `name`, `description`, `command`, `command2`, "
			+ "`attack`, `strength`, `hits`, `defense`, `ranged`, `combatlvl`, `isMembers`, `attackable`, `aggressive`, `respawnTime`, "
			+ "`sprites1`, `sprites2`, `sprites3`, `sprites4`, `sprites5`, `sprites6`, `sprites7`, `sprites8`, `sprites9`, "
			+ "`sprites10`, `sprites11`, `sprites12`, `hairColour`, `topColour`, `bottomColour`, `skinColour`, `camera1`, "
			+ "`camera2`, `walkModel`, `combatModel`, `combatSprite`, `roundMode`, `pkBot` FROM `"
			+ PREFIX + "npcdef` ORDER BY `id` ASC";
		npcDrops = "SELECT * FROM `" + PREFIX + "npcdrops`";
		itemDefs = "SELECT `id`, `name`, `description`, `command`, `isFemaleOnly`, `isMembersOnly`, `isStackable`, "
			+ "`isUntradable`, `isWearable`, `appearanceID`, `wearableID`, `wearSlot`, `requiredLevel`, `requiredSkillID`, "
			+ "`armourBonus`, `weaponAimBonus`, `weaponPowerBonus`, `magicBonus`, `prayerBonus`, `basePrice`, `isNoteable`"
			+ "FROM `" + PREFIX  + "itemdef` order by `id` ASC";
		banPlayer = "UPDATE `" + PREFIX + "players` SET `banned`=?, offences = offences + 1 WHERE `username` LIKE ?";
		unbanPlayer = "UPDATE `" + PREFIX + "players` SET `banned`= 0 WHERE `username` LIKE ?";
		initializeOnlineUsers = "UPDATE `" + PREFIX + "players` SET `online`='0' WHERE online='1'";
		fetchLoginIp = "SELECT `login_ip` FROM `" + PREFIX + "players` WHERE `username`=?";
		fetchLinkedPlayers = "SELECT `username`, `group_id` FROM `" + PREFIX + "players` WHERE `login_ip` LIKE ?";
		addNpcSpawn = "INSERT INTO `" + PREFIX + "npclocs`(`id`,`startX`,`minX`,`maxX`,`startY`,`minY`,`maxY`) VALUES(?, ?, ?, ?, ?, ?, ?)";
		removeNpcSpawn = "DELETE FROM `" + PREFIX + "npclocs` WHERE id=? AND startX=? AND startY=? AND minX=? AND maxX=? AND minY=? AND maxY=?";
		addObjectSpawn = "INSERT INTO `" + PREFIX + "objects`(`x`, `y`, `id`, `direction`, `type`) VALUES (?, ?, ?, ?, ?)";
		removeObjectSpawn = "DELETE FROM `" + PREFIX + "objects` WHERE x=? AND y=? AND id=? AND direction=? AND type=?";
		addItemSpawn = "INSERT INTO `" + PREFIX + "grounditems`(`id`, `x`, `y`, `amount`, `respawn`) VALUES (?, ?, ?, ?, ?)";
		removeItemSpawn = "DELETE FROM `" + PREFIX + "grounditems` WHERE id=? AND x=? AND y=?";

		clans = "SELECT `id`, `name`, `tag`, `kick_setting`, `invite_setting`, `allow_search_join`, `clan_points` FROM `" + PREFIX + "clan`";
		clanMembers = "SELECT `username`, `rank`, `kills`, `deaths` FROM `\" + getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + \"clan_players` WHERE `clan_id`=?";
		newClan = "INSERT INTO `" + PREFIX + "clan`(`name`, `tag`, `leader`) VALUES (?,?,?)";
		saveClanMember = "INSERT INTO `" + PREFIX + "clan_players`(`clan_id`, `username`, `rank`, `kills`, `deaths`) VALUES (?,?,?,?,?)";
		deleteClan = "DELETE FROM `" + PREFIX + "clan` WHERE `id`=?";
		deleteClanMembers = "DELETE FROM `" + PREFIX + "clan_players` WHERE `clan_id`=?";
		updateClan = "UPDATE `" + PREFIX + "clan` SET `name`=?, `tag`=?, `leader`=?, `kick_setting`=?, `invite_setting`=?, `allow_search_join`=?, `clan_points`=? WHERE `id`=?";
		updateClanMember = "UPDATE `" + PREFIX + "clan_players` SET `rank`=? WHERE `username`=?";

		//unreadMessages = "SELECT COUNT(*) FROM `messages` WHERE showed=0 AND show_message=1 AND owner=?";
		//teleportStones = "SELECT `teleport_stone` FROM `users` WHERE id=?";
	}
}
