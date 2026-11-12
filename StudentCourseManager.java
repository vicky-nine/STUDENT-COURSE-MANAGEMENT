import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StudentCourseManager {

    /* ------------------------------------------------------------------ */
    /* Model                                                              */
    /* ------------------------------------------------------------------ */
    static class Course {
        final String code;
        final String title;
        final int units;

        Course(String code, String title, int units) {
            this.code  = code.trim().toUpperCase();
            this.title = title.trim();
            this.units = units;
        }
    }

    private final List<Course> courses = new ArrayList<>();
    private final String filename = "courses.txt";   // replaces localStorage

    private JFrame frame;
    private JTextField codeInput, titleInput, unitsInput, searchInput;

    /* ------------------------------------------------------------------ */
    /* Entry point                                                        */
    /* ------------------------------------------------------------------ */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentCourseManager().createAndShowGui());
    }

    /* ------------------------------------------------------------------ */
    /* GUI                                                                */
    /* ------------------------------------------------------------------ */
    private void createAndShowGui() {
        frame = new JFrame("Student Course Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel heading = new JLabel("Student Course Management System");
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        heading.setForeground(new Color(0x333333));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(heading);
        container.add(Box.createVerticalStrut(18));

        /* ---- Row 1: add a course ---- */
        codeInput  = new JTextField(8);
        titleInput = new JTextField(14);
        unitsInput = new JTextField(4);

        JButton addBtn = new JButton("Add Course");
        addBtn.setBackground(new Color(0x1877F2));
        addBtn.setForeground(Color.WHITE);
        addBtn.setOpaque(true);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> addCourse());

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row1.setBackground(Color.WHITE);
        row1.add(codeInput);
        row1.add(titleInput);
        row1.add(unitsInput);
        row1.add(addBtn);
        container.add(row1);
        container.add(Box.createVerticalStrut(12));

        /* ---- Row 2: actions ---- */
        searchInput = new JTextField(10);

        JButton searchBtn  = new JButton("Search");
        JButton viewAllBtn = new JButton("View All");
        JButton totalBtn   = new JButton("Total Units");
        JButton saveBtn    = new JButton("Save");
        JButton loadBtn    = new JButton("Load");

        searchBtn.addActionListener(e  -> searchCourse());
        viewAllBtn.addActionListener(e -> viewAll());
        totalBtn.addActionListener(e   -> showTotal());
        saveBtn.addActionListener(e    -> saveToFile());
        loadBtn.addActionListener(e    -> loadFromFile());

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row2.setBackground(Color.WHITE);
        row2.add(searchInput);
        row2.add(searchBtn);
        row2.add(viewAllBtn);
        row2.add(totalBtn);
        row2.add(saveBtn);
        row2.add(loadBtn);
        container.add(row2);

        frame.setContentPane(container);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.getRootPane().setDefaultButton(addBtn);   // Enter = Add Course
    }

    /* ------------------------------------------------------------------ */
    /* The pop-up screen (a modal dialog, like the web modal)             */
    /* ------------------------------------------------------------------ */
    private void showPopup(String text) {
        JDialog dialog = new JDialog(frame, "Output Screen", true);
        dialog.setLayout(new BorderLayout());

        // Header bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x333333));

        JLabel title = new JLabel("Output Screen");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton close = new JButton("\u00D7");
        close.setForeground(new Color(0xAAAAAA));
        close.setBackground(new Color(0x333333));
        close.setBorderPainted(false);
        close.setFocusPainted(false);
        close.setFont(new Font("Arial", Font.PLAIN, 20));
        close.addActionListener(e -> dialog.dispose());

        header.add(title, BorderLayout.WEST);
        header.add(close, BorderLayout.EAST);

        // Dark output area
        JTextArea output = new JTextArea(text);
        output.setEditable(false);
        output.setBackground(new Color(0x1E1E1E));
        output.setForeground(new Color(0xD4D4D4));
        output.setCaretColor(Color.WHITE);
        output.setFont(new Font("Monospaced", Font.PLAIN, 14));
        output.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(560, 260));

        dialog.add(header, BorderLayout.NORTH);
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);        // blocks here until closed (modal)
    }

    /* ------------------------------------------------------------------ */
    /* Commands                                                           */
    /* ------------------------------------------------------------------ */
    private void addCourse() {
        String code  = codeInput.getText();
        String title = titleInput.getText();

        if (code.trim().isEmpty() || title.trim().isEmpty()) {
            showPopup("Error: code and title cannot be empty.");
            return;
        }

        int units;
        try {
            units = Integer.parseInt(unitsInput.getText().trim());
        } catch (NumberFormatException ex) {
            showPopup("Error: units must be a positive number.");
            return;
        }
        if (units <= 0) {
            showPopup("Error: units must be a positive number.");
            return;
        }

        String upper = code.trim().toUpperCase();
        for (Course c : courses) {
            if (c.code.equals(upper)) {
                showPopup("Error: " + upper + " already exists.");
                return;
            }
        }

        courses.add(new Course(code, title, units));
        showPopup("Successfully added: " + upper);

        codeInput.setText("");
        titleInput.setText("");
        unitsInput.setText("");
        codeInput.requestFocus();
    }

    private void viewAll() {
        if (courses.isEmpty()) {
            showPopup("No courses added yet.");
            return;
        }

        StringBuilder sb = new StringBuilder("--- All Courses ---\n\n");
        for (Course c : courses) {
            sb.append(String.format("%-10s | %-25s | %d units%n", c.code, c.title, c.units));
        }
        sb.append("\n-------------------");
        showPopup(sb.toString());
    }

    private void searchCourse() {
        String code = searchInput.getText();
        if (code.trim().isEmpty()) {
            showPopup("Please type a code in the search box first.");
            return;
        }

        String upper = code.trim().toUpperCase();
        for (Course c : courses) {
            if (c.code.equals(upper)) {
                showPopup("Found:\n\n" + c.code + " | " + c.title + " | " + c.units + " units");
                return;
            }
        }
        showPopup("Course " + upper + " not found.");
    }

    /** Recursive total, exactly like the JS version. */
    private int totalUnits(List<Course> list) {
        if (list.isEmpty()) {
            return 0;
        }
        return list.get(0).units + totalUnits(list.subList(1, list.size()));
    }

    private void showTotal() {
        showPopup("Total units: " + totalUnits(courses));
    }

    /* ------------------------------------------------------------------ */
    /* Persistence — a real file now instead of localStorage              */
    /* ------------------------------------------------------------------ */
    private void saveToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (Course c : courses) {
                out.println(c.code + "," + c.title + "," + c.units);
            }
            showPopup("Saved " + courses.size() + " courses to file.");
        } catch (IOException e) {
            showPopup("Error saving: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(filename);
        if (!file.exists()) {
            showPopup("No saved file found.");
            return;
        }

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            courses.clear();
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                try {
                    int units = Integer.parseInt(parts[2].trim());
                    courses.add(new Course(parts[0], parts[1], units));
                } catch (NumberFormatException ignored) {
                    // skip malformed line, same as the JS version
                }
            }
            showPopup("Loaded " + courses.size() + " courses from file.");
        } catch (IOException e) {
            showPopup("Error loading: " + e.getMessage());
        }
    }
}