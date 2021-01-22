package io.teamplayer.teamcore.holo;

import io.teamplayer.teamcore.util.ClientSideObject;
import io.teamplayer.teamcore.entity.fake.FakeEntity;
import io.teamplayer.teamcore.immutable.ImmutableLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

/**
 * Floating multi-lined name plates
 */
public class Hologram implements ClientSideObject {

    private final List<HoloLine> lines;
    private final Location location;

    private final Set<Player> viewers = new HashSet<>();
    private boolean global = true;

    public Hologram(Location location, String... lines) {
        this.lines = new ArrayList<>(lines.length);
        this.location = location;

        setLines(lines);
    }

    public void setLines(String... lines) {
        if (lines.length > this.lines.size()) {
            do {
                addLine();
            } while (lines.length != this.lines.size());
        } else if (lines.length < this.lines.size()) {
            do {
                removeLine();
            } while (lines.length != this.lines.size());
        }

        for (int i = 0; i < lines.length; i++) {
            setLine(i, lines[i]);
        }
    }

    public String[] getLines() {
        return lines.stream()
                .map(HoloLine::getName)
                .toArray(String[]::new);
    }

    private void setLine(int index, String content) {
        final HoloLine line = lines.get(index);

        if (line != null) {
            line.setName(content);
        }
    }

    private void addLine() {
        if (lines.size() < 1) {
            lines.add(new HoloLine("", location));
        } else {
            lines.add(new HoloLine("", ((ImmutableLocation) lines.get(lines.size() - 1)
                    .getLocation()).mutable().add(0,-0.4,0)));
        }
    }

    private void removeLine() {
        if (lines.size() > 1) {
            lines.get(lines.size() - 1).getArmorStand().destroy();
            lines.remove(lines.size() - 1);
        }
    }

    @Override
    public void spawn() {
        getFakeEntities().forEach(FakeEntity::spawn);
    }

    @Override
    public void destroy() {
        getFakeEntities().forEach(FakeEntity::destroy);
    }

    @Override
    public void addViewer(Player player) {
        getFakeEntities().forEach(e -> e.addViewer(player));
        viewers.add(player);
    }

    @Override
    public void removeViewer(Player player) {
        getFakeEntities().forEach(e -> e.removeViewer(player));
        viewers.remove(player);
    }

    @Override
    public Collection<Player> getViewers() {
        return Collections.unmodifiableCollection(viewers);
    }

    @Override
    public boolean isGlobal() {
        return global;
    }

    @Override
    public void setGlobal(boolean global) {
        this.global = global;
        getFakeEntities().forEach(e -> e.setGlobal(global));
    }

    private Stream<FakeEntity> getFakeEntities() {
        return lines.stream()
                .map(HoloLine::getArmorStand);
    }
}
