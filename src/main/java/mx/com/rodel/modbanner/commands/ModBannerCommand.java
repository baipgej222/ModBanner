package mx.com.rodel.modbanner.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import mx.com.rodel.modbanner.Helper;
import mx.com.rodel.modbanner.Main;

public class ModBannerCommand implements CommandCallable {

	HashMap<String, String> subCommands = new HashMap<>();

	public ModBannerCommand() {
		if (subCommands.isEmpty()) {
			subCommands.put("add", "Add mod to blacklist");
			subCommands.put("remove", "Remove mod from backlist");
			subCommands.put("list", "List all blacklisted mods");
			subCommands.put("reload", "Reload blaclist (Not needed if you do /add)");
		}
	}

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		Text.Builder rs = Text.builder();
		for (Entry<String, String> a : subCommands.entrySet()) {
			rs.append(Helper.format("&c/modbanner " + a.getKey() + " (" + a.getValue() + ")\n"));
		}
		return Optional.of(rs.build());
	}

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(!source.hasPermission("modbanner")){
			return CommandResult.empty();
		}
		String[] args = arguments.split(" ");

		switch (args[0].toLowerCase()) {
			case "add":
				if(args.length>=2){
					Main.instance.cfgManager.blackList.add(args[1]);
					Main.instance.cfgManager.save();
					Main.instance.reloadConfiguration();
					source.sendMessage(Helper.format("&a"+args[1]+" added to blacklist!"));
				}else{
					source.sendMessage(Helper.format("&cPlease add the mod name /modbanner add <mod>"));
				}
				break;
			case "remove":
				if(args.length>=2){
					if(Main.instance.cfgManager.blackList.contains(args[1])){
						Main.instance.cfgManager.blackList.remove(args[1]);
						source.sendMessage(Helper.format("&a"+args[1]+" removed from blacklist!"));
						Main.instance.cfgManager.save();
						Main.instance.reloadConfiguration();
					}else{
						source.sendMessage(Helper.format("&cCan't find mod "+args[1]));
					}
				}else{
					source.sendMessage(Helper.format("&cPlease add the mod name /modbanner remove <mod>"));
				}
				break;
			case "list":
				List<Text> t = new ArrayList<>();
				for(String bl : Main.instance.cfgManager.blackList){
					t.add(Helper.format(bl));
				}
				PaginationList.builder()
				.title(Helper.format("BlackListed Mods"))
				.contents(t)
				.padding(Text.of("-")).sendTo(source);
				break;
			case "reload":
				Main.instance.reloadConfiguration();
				source.sendMessage(Helper.format("&aConfiguration reloaded!"));
				break;
			default:
				source.sendMessage(getHelp(source).get());
				return CommandResult.empty();
		}
		
		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition)
			throws CommandException {
		return new ArrayList<>(subCommands.keySet());
	}

	@Override
	public boolean testPermission(CommandSource source) {
		return false;
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return Optional.empty();
	}

	@Override
	public Text getUsage(CommandSource source) {
		return Text.of(TextColors.RED,
				"modbanner <" + (subCommands.keySet().stream().collect(Collectors.joining("|"))) + ">");
	}
}
