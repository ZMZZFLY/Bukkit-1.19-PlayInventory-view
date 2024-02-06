package plus.zmzz.playerinventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryDisplayPlugin extends JavaPlugin {
    private JFrame frame;
    private JPanel playerPanel;
    private Map<String, List<ItemStack>> playerItems;
    private Map<String, JButton> playerButtons;
    private Map<String, Boolean> playerUpdates;
    private Map<String, JFrame> playerFrames;
    private Timer refreshTimer;

    @Override
    public void onEnable() {
        playerItems = new HashMap<>();
        playerButtons = new HashMap<>();
        playerUpdates = new HashMap<>();
        playerFrames = new HashMap<>();
        createUI();
        getServer().getPluginManager().registerEvents(new PlayerInventoryListener(), this);

        refreshTimer = new Timer();
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    List<ItemStack> items = Arrays.stream(player.getInventory().getStorageContents()).collect(Collectors.toList());
                    if (!items.equals(playerItems.get(player.getName()))) {
                        playerItems.put(player.getName(), items);
                        playerUpdates.put(player.getName(), true);

                        JFrame playerFrame = playerFrames.get(player.getName());
                        if (playerFrame != null) {
                            JPanel inventoryPanel = createInventoryPanel(playerItems.get(player.getName()));
                            playerFrame.getContentPane().removeAll();
                            playerFrame.getContentPane().add(inventoryPanel);
                            playerFrame.pack();
                            playerFrame.setVisible(true);
                        }
                    }
                }

                for (String playerName : playerUpdates.keySet()) {
                    boolean needsUpdate = playerUpdates.get(playerName);
                    if (needsUpdate) {
                        updateInventoryButton(playerName, playerItems.get(playerName));

                        JFrame playerFrame = playerFrames.get(playerName);
                        if (playerFrame != null) {
                            JPanel inventoryPanel = createInventoryPanel(playerItems.get(playerName));
                            playerFrame.getContentPane().removeAll();
                            playerFrame.getContentPane().add(inventoryPanel);
                            playerFrame.pack();
                            playerFrame.setVisible(true);
                        }

                        playerUpdates.put(playerName, false);
                    }
                }
            }
        }, 0L, 1000L);
    }

    @Override
    public void onDisable() {
        refreshTimer.cancel();
        playerItems.clear();
        playerButtons.clear();
        playerUpdates.clear();
        playerFrames.values().forEach(JFrame::dispose);
        frame.dispose();
    }

    private void createUI() {
        frame = new JFrame("查看");
        Box box = Box.createVerticalBox();

        JLabel title = new JLabel("玩家的背包");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        box.add(title);

        playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(playerPanel);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        box.add(scrollPane);

        frame.getContentPane().add(box);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private void updateInventoryDisplay() {
        playerItems.clear();
        playerButtons.clear();
        playerUpdates.clear();
        playerFrames.clear();
        playerPanel.removeAll();

        for (Player player : getServer().getOnlinePlayers()) {
            List<ItemStack> items = Arrays.stream(player.getInventory().getStorageContents()).collect(Collectors.toList());
            playerItems.put(player.getName(), items);
            playerUpdates.put(player.getName(), false);

            JLabel nameLabel = new JLabel(player.getName());
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            nameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            playerPanel.add(nameLabel);

            JButton viewButton = new JButton("查看背包");
            viewButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            viewButton.addActionListener(e -> displayInventory(player.getName()));
            playerButtons.put(player.getName(), viewButton);
            playerPanel.add(viewButton);

            int itemCount = (int) items.stream().filter(Objects::nonNull).count();
            viewButton.setText("查看背包 (" + itemCount + ")");
        }

        playerPanel.revalidate();
        playerPanel.repaint();
    }

    private void displayInventory(String playerName) {
        JFrame inventoryFrame = playerFrames.get(playerName);
        if (inventoryFrame != null) {
            JPanel inventoryPanel = createInventoryPanel(playerItems.get(playerName));
            inventoryFrame.getContentPane().removeAll();
            inventoryFrame.getContentPane().add(inventoryPanel);
            inventoryFrame.setBounds(0,0,600,500);
            inventoryFrame.pack();
            inventoryFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            inventoryFrame.setVisible(false);
        } else {
            inventoryFrame = new JFrame(playerName + "的背包");
            Box box = Box.createVerticalBox();

            JLabel nameLabel = new JLabel(playerName + "的背包");
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            box.add(nameLabel);

            JPanel inventoryPanel = createInventoryPanel(playerItems.get(playerName));
            box.add(inventoryPanel);

            inventoryFrame.getContentPane().add(box);
            inventoryFrame.setResizable(false);
            inventoryFrame.pack();
            inventoryFrame.setLocationRelativeTo(null);
            inventoryFrame.setVisible(true);

            playerFrames.put(playerName, inventoryFrame);
        }
    }

    private JPanel createInventoryPanel(List<ItemStack> items) {
        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.X_AXIS));
        int itemCount = 0;
        for (ItemStack item : items) {
            if (item != null) {
                JButton button = new JButton(item.getType().name().toLowerCase());
                button.setToolTipText(item.getType().name());
                inventoryPanel.add(button);
                itemCount++;
            }
        }
        if (itemCount == 0) {
            JLabel emptyLabel = new JLabel("背包为空");
            emptyLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            inventoryPanel.add(emptyLabel);
        }
        inventoryPanel.setPreferredSize(new Dimension(600, 500));
        inventoryPanel.setMaximumSize(new Dimension(600, 500));
        return inventoryPanel;
    }

    private void updateInventoryButton(String playerName, List<ItemStack> items) {
        int itemCount = (int) items.stream().filter(Objects::nonNull).count();
        JButton button = playerButtons.get(playerName);
        button.setText("查看背包 (" + itemCount + ")");
    }

    private class PlayerInventoryListener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            updateInventoryDisplay();
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            JFrame playerFrame = playerFrames.remove(event.getPlayer().getName());
            if (playerFrame != null) {
                playerFrame.dispose();
            }
            playerItems.remove(event.getPlayer().getName());
            playerButtons.remove(event.getPlayer().getName());
            playerUpdates.remove(event.getPlayer().getName());
            updateInventoryDisplay();
        }
    }

}
