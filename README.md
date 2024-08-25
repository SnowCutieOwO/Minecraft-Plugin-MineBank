# Minecraft-Plugin-MineBank

Allows to storage money in bank and win money pasively. Add the command /bank.

Compiled with Java 8

Vault and some economics plugin that supports vault are needed.
If you modify config.yml, you need to use "reload" in cosole or restart the server for the change to take effect.

COMMANDS
Aliases: minebank, mb
/minebank help, Show minebank command list (admin)
/minebank reload, Reload plugin config (admin)
/minebank version, Show plugin version (admin)
/minebank author, Show plugin author (admin)
/minebank permissions, Show plugin permission list (admin)
/minebank plugin, Show plugin download link (admin)

/bank help, Show bank command list
/bank add <amount>, Deposit money
/bank take <amount>, Withdraw money
/bank set <player> bal <amount>, Set player balance (admin)
/bank set <player> level <amount>, Set player level (admin)
/bank top, Show top 10 players with the most money
/bank bal, Check your balance
/bank bal <player>, Check another player`s balance
/bank max bal, Check maximum capacity of your bank level
/bank max level, Check maximum level of the bank
/bank level, Check your bank level
/bank levelup, Level up your bank account


PERMISSIONS
minebank.* -- OP permissions
minebank.help -- Allow to use /minebank help
minebank.reload -- Allow to use /minebank reload
minebank.version -- Allow to use /minebank version
minebank.author -- Allow to use /minebank author
minebank.plugin -- Allow to use /minebank plugin
minebank.permissions -- Allow to use /minebank permissions
minebank.updatechecker -- Allow receive message if there is an update
minebank.admin -- Allow to use all (admin) commands
minebank.use -- Allow to use the bank
minebank.helpbank -- Allow to use /bank help
