package Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("set", "create", "buy", "confirm")
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return Stream.of("pos1", "pos2")
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            List<String> completions = new ArrayList<>();
            completions.add("<имя_дома>");
            return completions;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            List<String> completions = new ArrayList<>();
            completions.add("<цена>");
            return completions;
        }

        return new ArrayList<>();
    }
}