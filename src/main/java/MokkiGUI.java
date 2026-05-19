import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import java.net.URL;

public class MokkiGUI extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mokkikodit";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "salasana";

    // Asiakas tab components
    private TableView<Asiakas> asiakasTable;
    private TextField asiakasEtuNimi, asiakasSukuNimi, asiakasPuhNro, asiakasSposti, asiakasOsoite;
    private ObservableList<Asiakas> asiakasData = FXCollections.observableArrayList();

    // Mökki tab components
    private TableView<Mokki> mokkiTable;
    private TextField mokkiNimi, mokkiSijainti, mokkiKapasiteetti, mokkiHinta;
    private ObservableList<Mokki> mokkiData = FXCollections.observableArrayList();

    // Työntekijä tab components
    private TableView<Tyontekija> tyontekijaTable;
    private TextField tyontekijaEtuNimi, tyontekijaSukuNimi, tyontekijaSposti;
    private ObservableList<Tyontekija> tyontekijaData = FXCollections.observableArrayList();

    // Varaus tab components
    private TableView<Varaus> varausTable;
    private ComboBox<String> varausAsiakasCombo, varausMokkiCombo, varausTyontekijaCombo;
    private DatePicker varausAlkuPvm, varausLoppuPvm;
    private ObservableList<Varaus> varausData = FXCollections.observableArrayList();

    // Lasku tab components
    private TableView<Lasku> laskuTable;
    private ObservableList<Lasku> laskuData = FXCollections.observableArrayList();

    private DatePicker laskutAlkuPvm, laskutLoppuPvm;
    private Label totalRevenueLabel, totalBookingsLabel, popularCabinLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mökkikodit - Varausjärjestelmä");

        TabPane tabPane = new TabPane();

        // Create tabs
        Tab asiakkaatTab = new Tab("Asiakkaat", buildAsiakkaatTab());
        Tab mokitTab = new Tab("Mökit", buildMokitTab());
        Tab tyontekijatTab = new Tab("Työntekijät", buildTyontekijatTab());
        Tab varauksetTab = new Tab("Varaukset", buildVarauksetTab());
        Tab laskutTab = new Tab("Laskut", buildLaskutTab());

        asiakkaatTab.setClosable(false);
        mokitTab.setClosable(false);
        tyontekijatTab.setClosable(false);
        varauksetTab.setClosable(false);
        laskutTab.setClosable(false);

        tabPane.getTabs().addAll(asiakkaatTab, mokitTab, tyontekijatTab, varauksetTab, laskutTab);

        Scene scene = new Scene(tabPane, 900, 600);

        URL cssUrl = getClass().getResource("/style.css");

        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("CSS file not found!");
        }

        primaryStage.setScene(scene);
        primaryStage.show();

        // Load initial data
        loadAsiakkaat();
        loadMokit();
        loadTyontekijat();
        loadVaraukset();
        loadLaskut();
    }

    // ============ ASIAKKAAT TAB ============

    private VBox buildAsiakkaatTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        // Table
        asiakasTable = new TableView<>();

        asiakasTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        asiakasTable.setItems(asiakasData);

        TableColumn<Asiakas, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Asiakas, String> etuNimiCol = new TableColumn<>("Etunimi");
        etuNimiCol.setCellValueFactory(new PropertyValueFactory<>("etuNimi"));
        etuNimiCol.setPrefWidth(120);

        TableColumn<Asiakas, String> sukuNimiCol = new TableColumn<>("Sukunimi");
        sukuNimiCol.setCellValueFactory(new PropertyValueFactory<>("sukuNimi"));
        sukuNimiCol.setPrefWidth(120);

        TableColumn<Asiakas, String> puhNroCol = new TableColumn<>("Puhelinnumero");
        puhNroCol.setCellValueFactory(new PropertyValueFactory<>("puhNro"));
        puhNroCol.setPrefWidth(120);

        TableColumn<Asiakas, String> spostiCol = new TableColumn<>("Sähköposti");
        spostiCol.setCellValueFactory(new PropertyValueFactory<>("sposti"));
        spostiCol.setPrefWidth(180);

        TableColumn<Asiakas, String> osoiteCol = new TableColumn<>("Osoite");
        osoiteCol.setCellValueFactory(new PropertyValueFactory<>("osoite"));
        osoiteCol.setPrefWidth(200);

        asiakasTable.getColumns().addAll(idCol, etuNimiCol, sukuNimiCol, puhNroCol, spostiCol, osoiteCol);

        // Form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        asiakasEtuNimi = new TextField();
        asiakasSukuNimi = new TextField();
        asiakasPuhNro = new TextField();
        asiakasSposti = new TextField();
        asiakasOsoite = new TextField();

        form.add(new Label("Etunimi:"), 0, 0);
        form.add(asiakasEtuNimi, 1, 0);
        form.add(new Label("Sukunimi:"), 0, 1);
        form.add(asiakasSukuNimi, 1, 1);
        form.add(new Label("Puhelinnumero:"), 0, 2);
        form.add(asiakasPuhNro, 1, 2);
        form.add(new Label("Sähköposti:"), 0, 3);
        form.add(asiakasSposti, 1, 3);
        form.add(new Label("Osoite:"), 0, 4);
        form.add(asiakasOsoite, 1, 4);

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addBtn = new Button("Lisää asiakas");
        Button updateBtn = new Button("Päivitä asiakas");
        Button deleteBtn = new Button("Poista asiakas");
        Button clearBtn = new Button("Tyhjennä lomake");

        addBtn.setOnAction(e -> addAsiakas());
        updateBtn.setOnAction(e -> updateAsiakas());
        deleteBtn.setOnAction(e -> deleteAsiakas());
        clearBtn.setOnAction(e -> clearAsiakasForm());

        buttonBox.getChildren().addAll(addBtn, updateBtn, deleteBtn, clearBtn);

        // Table selection
        asiakasTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                asiakasEtuNimi.setText(newSelection.getEtuNimi());
                asiakasSukuNimi.setText(newSelection.getSukuNimi());
                asiakasPuhNro.setText(newSelection.getPuhNro());
                asiakasSposti.setText(newSelection.getSposti());
                asiakasOsoite.setText(newSelection.getOsoite());
            }
        });

        layout.getChildren().addAll(new Label("Asiakkaat"), asiakasTable, new Label("Asiakkaan tiedot"), form,
                buttonBox);
        return layout;
    }

    private void loadAsiakkaat() {
        asiakasData.clear();
        String query = "SELECT * FROM asiakas";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                asiakasData.add(new Asiakas(
                        rs.getInt("asiakas_id"),
                        rs.getString("etu_nimi"),
                        rs.getString("suku_nimi"),
                        rs.getString("puh_nro"),
                        rs.getString("sposti"),
                        rs.getString("osoite")));
            }
        } catch (SQLException e) {
            showError("Virhe ladattaessa asiakkaita: " + e.getMessage());
        }
    }

    private void addAsiakas() {
        if (asiakasEtuNimi.getText().isEmpty() || asiakasSukuNimi.getText().isEmpty()
                || asiakasOsoite.getText().isEmpty()) {
            showError("Etunimi, sukunimi ja osoite ovat pakollisia!");
            return;
        }

        String query = "INSERT INTO asiakas (etu_nimi, suku_nimi, puh_nro, sposti, osoite) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, asiakasEtuNimi.getText());
            pstmt.setString(2, asiakasSukuNimi.getText());
            pstmt.setString(3, asiakasPuhNro.getText());
            pstmt.setString(4, asiakasSposti.getText());
            pstmt.setString(5, asiakasOsoite.getText());

            pstmt.executeUpdate();
            loadAsiakkaat();
            loadVarausComboBoxes();
            clearAsiakasForm();
            showInfo("Asiakas lisätty onnistuneesti!");

        } catch (SQLException e) {
            showError("Virhe lisättäessä asiakasta: " + e.getMessage());
        }
    }

    private void updateAsiakas() {
        Asiakas selected = asiakasTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Valitse päivitettävä asiakas taulukosta!");
            return;
        }

        String query = "UPDATE asiakas SET etu_nimi=?, suku_nimi=?, puh_nro=?, sposti=?, osoite=? WHERE asiakas_id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, asiakasEtuNimi.getText());
            pstmt.setString(2, asiakasSukuNimi.getText());
            pstmt.setString(3, asiakasPuhNro.getText());
            pstmt.setString(4, asiakasSposti.getText());
            pstmt.setString(5, asiakasOsoite.getText());
            pstmt.setInt(6, selected.getId());

            pstmt.executeUpdate();
            loadAsiakkaat();
            loadVarausComboBoxes();
            clearAsiakasForm();
            showInfo("Asiakas päivitetty onnistuneesti!");

        } catch (SQLException e) {
            showError("Virhe päivitettäessä asiakasta: " + e.getMessage());
        }
    }

    private void deleteAsiakas() {
        Asiakas selected = asiakasTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Valitse poistettava asiakas taulukosta!");
            return;
        }

        String query = "DELETE FROM asiakas WHERE asiakas_id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();
            loadAsiakkaat();
            loadVarausComboBoxes();
            clearAsiakasForm();
            showInfo("Asiakas poistettu onnistuneesti!");

        } catch (SQLException e) {
            showError("Virhe poistettaessa asiakasta: " + e.getMessage());
        }
    }

    private void clearAsiakasForm() {
        asiakasEtuNimi.clear();
        asiakasSukuNimi.clear();
        asiakasPuhNro.clear();
        asiakasSposti.clear();
        asiakasOsoite.clear();
        asiakasTable.getSelectionModel().clearSelection();
    }

    // ============ MÖKIT TAB ============

    private VBox buildMokitTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        // Table
        mokkiTable = new TableView<>();

        mokkiTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mokkiTable.setItems(mokkiData);

        TableColumn<Mokki, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Mokki, String> nimiCol = new TableColumn<>("Nimi");
        nimiCol.setCellValueFactory(new PropertyValueFactory<>("nimi"));
        nimiCol.setPrefWidth(150);

        TableColumn<Mokki, String> sijaintiCol = new TableColumn<>("Sijainti");
        sijaintiCol.setCellValueFactory(new PropertyValueFactory<>("sijainti"));
        sijaintiCol.setPrefWidth(200);

        TableColumn<Mokki, Integer> kapasiteettiCol = new TableColumn<>("Kapasiteetti");
        kapasiteettiCol.setCellValueFactory(new PropertyValueFactory<>("kapasiteetti"));
        kapasiteettiCol.setPrefWidth(100);

        TableColumn<Mokki, Double> hintaCol = new TableColumn<>("Hinta (€)");
        hintaCol.setCellValueFactory(new PropertyValueFactory<>("hinta"));
        hintaCol.setPrefWidth(100);

        mokkiTable.getColumns().addAll(idCol, nimiCol, sijaintiCol, kapasiteettiCol, hintaCol);

        // Form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        mokkiNimi = new TextField();
        mokkiSijainti = new TextField();
        mokkiKapasiteetti = new TextField();
        mokkiHinta = new TextField();

        form.add(new Label("Nimi:"), 0, 0);
        form.add(mokkiNimi, 1, 0);
        form.add(new Label("Sijainti:"), 0, 1);
        form.add(mokkiSijainti, 1, 1);
        form.add(new Label("Kapasiteetti:"), 0, 2);
        form.add(mokkiKapasiteetti, 1, 2);
        form.add(new Label("Hinta (€):"), 0, 3);
        form.add(mokkiHinta, 1, 3);

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addBtn = new Button("Lisää mökki");
        Button updateBtn = new Button("Päivitä mökki");
        Button deleteBtn = new Button("Poista mökki");
        Button clearBtn = new Button("Tyhjennä lomake");

        addBtn.setOnAction(e -> addMokki());
        updateBtn.setOnAction(e -> updateMokki());
        deleteBtn.setOnAction(e -> deleteMokki());
        clearBtn.setOnAction(e -> clearMokkiForm());

        buttonBox.getChildren().addAll(addBtn, updateBtn, deleteBtn, clearBtn);

        // Table selection
        mokkiTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mokkiNimi.setText(newSelection.getNimi());
                mokkiSijainti.setText(newSelection.getSijainti());
                mokkiKapasiteetti.setText(String.valueOf(newSelection.getKapasiteetti()));
                mokkiHinta.setText(String.valueOf(newSelection.getHinta()));
            }
        });

        layout.getChildren().addAll(new Label("Mökit"), mokkiTable, new Label("Mökin tiedot"), form, buttonBox);
        return layout;
    }

    private void loadMokit() {
        mokkiData.clear();
        String query = "SELECT * FROM mokki";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                mokkiData.add(new Mokki(
                        rs.getInt("mokki_id"),
                        rs.getString("nimi"),
                        rs.getString("sijainti"),
                        rs.getInt("kapasiteetti"),
                        rs.getDouble("hinta")));
            }
        } catch (SQLException e) {
            showError("Virhe ladattaessa mökkejä: " + e.getMessage());
        }
    }

    private void addMokki() {
        if (mokkiNimi.getText().isEmpty() || mokkiSijainti.getText().isEmpty()) {
            showError("Nimi ja sijainti ovat pakollisia!");
            return;
        }

        try {
            int kapasiteetti = Integer.parseInt(mokkiKapasiteetti.getText());
            double hinta = Double.parseDouble(mokkiHinta.getText());

            String query = "INSERT INTO mokki (nimi, sijainti, kapasiteetti, hinta) VALUES (?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                    PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, mokkiNimi.getText());
                pstmt.setString(2, mokkiSijainti.getText());
                pstmt.setInt(3, kapasiteetti);
                pstmt.setDouble(4, hinta);

                pstmt.executeUpdate();
                loadMokit();
                loadVarausComboBoxes();
                clearMokkiForm();
                showInfo("Mökki lisätty onnistuneesti!");
            }

        } catch (NumberFormatException e) {
            showError("Kapasiteetti ja hinta täytyy olla numeroita!");
        } catch (SQLException e) {
            showError("Virhe lisättäessä mökkiä: " + e.getMessage());
        }
    }

    private void updateMokki() {
        Mokki selected = mokkiTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Valitse päivitettävä mökki taulukosta!");
            return;
        }

        try {
            int kapasiteetti = Integer.parseInt(mokkiKapasiteetti.getText());
            double hinta = Double.parseDouble(mokkiHinta.getText());

            String query = "UPDATE mokki SET nimi=?, sijainti=?, kapasiteetti=?, hinta=? WHERE mokki_id=?";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                    PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, mokkiNimi.getText());
                pstmt.setString(2, mokkiSijainti.getText());
                pstmt.setInt(3, kapasiteetti);
                pstmt.setDouble(4, hinta);
                pstmt.setInt(5, selected.getId());

                pstmt.executeUpdate();
                loadMokit();
                clearMokkiForm();
                showInfo("Mökki päivitetty onnistuneesti!");
            }

        } catch (NumberFormatException e) {
            showError("Kapasiteetti ja hinta täytyy olla numeroita!");
        } catch (SQLException e) {
            showError("Virhe päivitettäessä mökkiä: " + e.getMessage());
        }
    }

    private void deleteMokki() {
        Mokki selected = mokkiTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Valitse poistettava mökki taulukosta!");
            return;
        }

        String query = "DELETE FROM mokki WHERE mokki_id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();
            loadMokit();
            loadVarausComboBoxes();
            clearMokkiForm();
            showInfo("Mökki poistettu onnistuneesti!");

        } catch (SQLException e) {
            showError("Virhe poistettaessa mökkiä: " + e.getMessage());
        }
    }

    private void clearMokkiForm() {
        mokkiNimi.clear();
        mokkiSijainti.clear();
        mokkiKapasiteetti.clear();
        mokkiHinta.clear();
        mokkiTable.getSelectionModel().clearSelection();
    }

    // ============ TYÖNTEKIJÄT TAB ============

    private VBox buildTyontekijatTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        // Table
        tyontekijaTable = new TableView<>();

        tyontekijaTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tyontekijaTable.setItems(tyontekijaData);

        TableColumn<Tyontekija, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Tyontekija, String> etuNimiCol = new TableColumn<>("Etunimi");
        etuNimiCol.setCellValueFactory(new PropertyValueFactory<>("etuNimi"));
        etuNimiCol.setPrefWidth(150);

        TableColumn<Tyontekija, String> sukuNimiCol = new TableColumn<>("Sukunimi");
        sukuNimiCol.setCellValueFactory(new PropertyValueFactory<>("sukuNimi"));
        sukuNimiCol.setPrefWidth(150);

        TableColumn<Tyontekija, String> spostiCol = new TableColumn<>("Sähköposti");
        spostiCol.setCellValueFactory(new PropertyValueFactory<>("sposti"));
        spostiCol.setPrefWidth(200);

        tyontekijaTable.getColumns().addAll(idCol, etuNimiCol, sukuNimiCol, spostiCol);

        // Form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        tyontekijaEtuNimi = new TextField();
        tyontekijaSukuNimi = new TextField();
        tyontekijaSposti = new TextField();

        form.add(new Label("Etunimi:"), 0, 0);
        form.add(tyontekijaEtuNimi, 1, 0);
        form.add(new Label("Sukunimi:"), 0, 1);
        form.add(tyontekijaSukuNimi, 1, 1);
        form.add(new Label("Sähköposti:"), 0, 2);
        form.add(tyontekijaSposti, 1, 2);

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addBtn = new Button("Lisää työntekijä");
        Button updateBtn = new Button("Päivitä työntekijä");
        Button deleteBtn = new Button("Poista työntekijä");
        Button clearBtn = new Button("Tyhjennä lomake");

        addBtn.setOnAction(e -> addTyontekija());
        updateBtn.setOnAction(e -> updateTyontekija());
        deleteBtn.setOnAction(e -> deleteTyontekija());
        clearBtn.setOnAction(e -> clearTyontekijaForm());

        buttonBox.getChildren().addAll(addBtn, updateBtn, deleteBtn, clearBtn);

        // Table selection
        tyontekijaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                tyontekijaEtuNimi.setText(newSelection.getEtuNimi());
                tyontekijaSukuNimi.setText(newSelection.getSukuNimi());
                tyontekijaSposti.setText(newSelection.getSposti());
            }
        });

        layout.getChildren().addAll(new Label("Työntekijät"), tyontekijaTable, new Label("Työntekijän tiedot"), form,
                buttonBox);
        return layout;
    }

    private void loadTyontekijat() {
        tyontekijaData.clear();
        String query = "SELECT * FROM tyontekija";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                tyontekijaData.add(new Tyontekija(
                        rs.getInt("tyontekija_id"),
                        rs.getString("etu_nimi"),
                        rs.getString("suku_nimi"),
                        rs.getString("sposti")));
            }
        } catch (SQLException e) {
            showError("Virhe ladattaessa työntekijöitä: " + e.getMessage());
        }
    }

    private void addTyontekija() {
        if (tyontekijaEtuNimi.getText().isEmpty() || tyontekijaSukuNimi.getText().isEmpty()) {
            showError("Etunimi ja sukunimi ovat pakollisia!");
            return;
        }

        String query = "INSERT INTO tyontekija (etu_nimi, suku_nimi, sposti) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, tyontekijaEtuNimi.getText());
            pstmt.setString(2, tyontekijaSukuNimi.getText());
            pstmt.setString(3, tyontekijaSposti.getText());

            pstmt.executeUpdate();
            loadTyontekijat();
            loadVarausComboBoxes();
            clearTyontekijaForm();
            showInfo("Työntekijä lisätty onnistuneesti!");

        } catch (SQLException e) {
            showError("Virhe lisättäessä työntekijää: " + e.getMessage());
        }
    }

    private void updateTyontekija() {
        Tyontekija selected = tyontekijaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Valitse päivitettävä työntekijä taulukosta!");
            return;
        }

        String query = "UPDATE tyontekija SET etu_nimi=?, suku_nimi=?, sposti=? WHERE tyontekija_id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, tyontekijaEtuNimi.getText());
            pstmt.setString(2, tyontekijaSukuNimi.getText());
            pstmt.setString(3, tyontekijaSposti.getText());
            pstmt.setInt(4, selected.getId());

            pstmt.executeUpdate();
            loadTyontekijat();
            loadVarausComboBoxes();
            clearTyontekijaForm();
            showInfo("Työntekijä päivitetty onnistuneesti!");

        } catch (SQLException e) {
            showError("Virhe päivitettäessä työntekijää: " + e.getMessage());
        }
    }

    private void deleteTyontekija() {
        Tyontekija selected = tyontekijaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Valitse poistettava työntekijä taulukosta!");
            return;
        }

        String query = "DELETE FROM tyontekija WHERE tyontekija_id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();
            loadTyontekijat();
            loadVarausComboBoxes();
            clearTyontekijaForm();
            showInfo("Työntekijä poistettu onnistuneesti!");

        } catch (SQLException e) {
            showError("Virhe poistettaessa työntekijää: " + e.getMessage());
        }
    }

    private void clearTyontekijaForm() {
        tyontekijaEtuNimi.clear();
        tyontekijaSukuNimi.clear();
        tyontekijaSposti.clear();
        tyontekijaTable.getSelectionModel().clearSelection();
    }

    // ============ VARAUKSET TAB ============

    private VBox buildVarauksetTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        // Table
        varausTable = new TableView<>();

        varausTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        varausTable.setItems(varausData);

        TableColumn<Varaus, Integer> idCol = new TableColumn<>("Varaus ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);

        TableColumn<Varaus, String> asiakasCol = new TableColumn<>("Asiakas");
        asiakasCol.setCellValueFactory(new PropertyValueFactory<>("asiakasNimi"));
        asiakasCol.setPrefWidth(150);

        TableColumn<Varaus, String> mokkiCol = new TableColumn<>("Mökki");
        mokkiCol.setCellValueFactory(new PropertyValueFactory<>("mokkiNimi"));
        mokkiCol.setPrefWidth(150);

        TableColumn<Varaus, String> tyontekijaCol = new TableColumn<>("Työntekijä");
        tyontekijaCol.setCellValueFactory(new PropertyValueFactory<>("tyontekijaNimi"));
        tyontekijaCol.setPrefWidth(150);

        TableColumn<Varaus, String> alkuCol = new TableColumn<>("Alkupäivä");
        alkuCol.setCellValueFactory(new PropertyValueFactory<>("alkuPvm"));
        alkuCol.setPrefWidth(100);

        TableColumn<Varaus, String> loppuCol = new TableColumn<>("Loppupäivä");
        loppuCol.setCellValueFactory(new PropertyValueFactory<>("loppuPvm"));
        loppuCol.setPrefWidth(100);

        varausTable.getColumns().addAll(idCol, asiakasCol, mokkiCol, tyontekijaCol, alkuCol, loppuCol);

        // Form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        varausAsiakasCombo = new ComboBox<>();
        varausMokkiCombo = new ComboBox<>();
        varausTyontekijaCombo = new ComboBox<>();
        varausAlkuPvm = new DatePicker();
        varausLoppuPvm = new DatePicker();

        form.add(new Label("Asiakas:"), 0, 0);
        form.add(varausAsiakasCombo, 1, 0);
        form.add(new Label("Mökki:"), 0, 1);
        form.add(varausMokkiCombo, 1, 1);
        form.add(new Label("Työntekijä:"), 0, 2);
        form.add(varausTyontekijaCombo, 1, 2);
        form.add(new Label("Alkupäivä:"), 0, 3);
        form.add(varausAlkuPvm, 1, 3);
        form.add(new Label("Loppupäivä:"), 0, 4);
        form.add(varausLoppuPvm, 1, 4);

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addBtn = new Button("Luo varaus");
        Button updateBtn = new Button("Päivitä varaus");
        Button deleteBtn = new Button("Poista varaus");
        Button clearBtn = new Button("Tyhjennä lomake");

        addBtn.setOnAction(e -> addVaraus());
        updateBtn.setOnAction(e -> updateVaraus());
        deleteBtn.setOnAction(e -> deleteVaraus());
        clearBtn.setOnAction(e -> clearVarausForm());

        buttonBox.getChildren().addAll(addBtn, updateBtn, deleteBtn, clearBtn);

        varausTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                // Set customer
                for (String item : varausAsiakasCombo.getItems()) {
                    if (item.contains(newSelection.getAsiakasNimi())) {
                        varausAsiakasCombo.setValue(item);
                        break;
                    }
                }

                // Set cabin
                for (String item : varausMokkiCombo.getItems()) {
                    if (item.contains(newSelection.getMokkiNimi())) {
                        varausMokkiCombo.setValue(item);
                        break;
                    }
                }

                // Set employee
                for (String item : varausTyontekijaCombo.getItems()) {
                    if (item.contains(newSelection.getTyontekijaNimi())) {
                        varausTyontekijaCombo.setValue(item);
                        break;
                    }
                }

                // Set dates
                varausAlkuPvm.setValue(LocalDate.parse(newSelection.getAlkuPvm()));
                varausLoppuPvm.setValue(LocalDate.parse(newSelection.getLoppuPvm()));
            }
        });

        layout.getChildren().addAll(new Label("Varaukset"), varausTable, new Label("Uusi varaus"), form, buttonBox);

        // Load combo box data
        loadVarausComboBoxes();

        return layout;
    }

    private void loadVarausComboBoxes() {
        // Load customers
        ObservableList<String> asiakkaat = FXCollections.observableArrayList();
        String asiakasQuery = "SELECT asiakas_id, etu_nimi, suku_nimi FROM asiakas";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(asiakasQuery)) {

            while (rs.next()) {
                asiakkaat.add(rs.getInt("asiakas_id") + " - " +
                        rs.getString("etu_nimi") + " " + rs.getString("suku_nimi"));
            }
        } catch (SQLException e) {
            showError("Virhe ladattaessa asiakkaita: " + e.getMessage());
        }
        varausAsiakasCombo.setItems(asiakkaat);

        // Load cabins
        ObservableList<String> mokit = FXCollections.observableArrayList();
        String mokkiQuery = "SELECT mokki_id, nimi, sijainti FROM mokki";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(mokkiQuery)) {

            while (rs.next()) {
                mokit.add(rs.getInt("mokki_id") + " - " +
                        rs.getString("nimi") + " (" + rs.getString("sijainti") + ")");
            }
        } catch (SQLException e) {
            showError("Virhe ladattaessa mökkejä: " + e.getMessage());
        }
        varausMokkiCombo.setItems(mokit);

        // Load employees
        ObservableList<String> tyontekijat = FXCollections.observableArrayList();
        String tyontekijaQuery = "SELECT tyontekija_id, etu_nimi, suku_nimi FROM tyontekija";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(tyontekijaQuery)) {

            while (rs.next()) {
                tyontekijat.add(rs.getInt("tyontekija_id") + " - " +
                        rs.getString("etu_nimi") + " " + rs.getString("suku_nimi"));
            }
        } catch (SQLException e) {
            showError("Virhe ladattaessa työntekijöitä: " + e.getMessage());
        }
        varausTyontekijaCombo.setItems(tyontekijat);
    }

    private void loadVaraukset() {
        varausData.clear();
        String query = "SELECT v.varaus_id, v.alku_pvm, v.loppu_pvm, " +
                "a.etu_nimi AS a_etu, a.suku_nimi AS a_suku, " +
                "m.nimi AS mokki_nimi, " +
                "t.etu_nimi AS t_etu, t.suku_nimi AS t_suku " +
                "FROM varaus v " +
                "JOIN asiakas a ON v.asiakas_id = a.asiakas_id " +
                "JOIN mokki m ON v.mokki_id = m.mokki_id " +
                "JOIN tyontekija t ON v.tyontekija_id = t.tyontekija_id";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                varausData.add(new Varaus(
                        rs.getInt("varaus_id"),
                        rs.getString("a_etu") + " " + rs.getString("a_suku"),
                        rs.getString("mokki_nimi"),
                        rs.getString("t_etu") + " " + rs.getString("t_suku"),
                        rs.getString("alku_pvm"),
                        rs.getString("loppu_pvm")));
            }
        } catch (SQLException e) {
            showError("Virhe ladattaessa varauksia: " + e.getMessage());
        }
    }

    private void addVaraus() {
        if (varausAsiakasCombo.getValue() == null || varausMokkiCombo.getValue() == null ||
                varausTyontekijaCombo.getValue() == null || varausAlkuPvm.getValue() == null ||
                varausLoppuPvm.getValue() == null) {
            showError("Kaikki kentät ovat pakollisia!");
            return;
        }

        // Extract IDs from combo box values
        int asiakasId = Integer.parseInt(varausAsiakasCombo.getValue().split(" - ")[0]);
        int mokkiId = Integer.parseInt(varausMokkiCombo.getValue().split(" - ")[0]);
        int tyontekijaId = Integer.parseInt(varausTyontekijaCombo.getValue().split(" - ")[0]);

        // Calculate number of days and total price
        long days = java.time.temporal.ChronoUnit.DAYS.between(varausAlkuPvm.getValue(), varausLoppuPvm.getValue());
        if (days <= 0) {
            showError("Loppupäivän täytyy olla alkupäivän jälkeen!");
            return;
        }

        // Get cabin price
        double cabinPrice = 0;
        String priceQuery = "SELECT hinta FROM mokki WHERE mokki_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pstmt = conn.prepareStatement(priceQuery)) {
            pstmt.setInt(1, mokkiId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                
                // Assign the result from the database to variable
                cabinPrice = rs.getDouble("hinta");
                
            }
        } catch (SQLException e) {
            showError("Virhe haettaessa mökin hintaa: " + e.getMessage());
            return;
        }

        double totalPrice = cabinPrice * days;

        // Create reservation
        String varausQuery = "INSERT INTO varaus (asiakas_id, mokki_id, tyontekija_id, alku_pvm, loppu_pvm) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pstmt = conn.prepareStatement(varausQuery, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, asiakasId);
            pstmt.setInt(2, mokkiId);
            pstmt.setInt(3, tyontekijaId);
            pstmt.setDate(4, Date.valueOf(varausAlkuPvm.getValue()));
            pstmt.setDate(5, Date.valueOf(varausLoppuPvm.getValue()));

            pstmt.executeUpdate();

            // Get the generated varaus_id
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int varausId = generatedKeys.getInt(1);

                // Create invoice automatically
                // Due date is 14 days from today
                LocalDate dueDate = varausLoppuPvm.getValue().plusDays(14);

                String laskuQuery = "INSERT INTO lasku (summa, erapaiva, varaus_id) VALUES (?, ?, ?)";
                try (PreparedStatement laskuPstmt = conn.prepareStatement(laskuQuery)) {
                    laskuPstmt.setDouble(1, totalPrice);
                    laskuPstmt.setDate(2, Date.valueOf(dueDate));
                    laskuPstmt.setInt(3, varausId);
                    laskuPstmt.executeUpdate();
                }
            }

            loadVaraukset();
            loadLaskut(); // Also refresh the invoices
            clearVarausForm();
            showInfo("Varaus ja lasku luotu onnistuneesti! Summa: " + String.format("%.2f€", totalPrice));

        } catch (SQLException e) {
            showError("Virhe luotaessa varausta: " + e.getMessage());
        }
    }

    private void updateVaraus() {

        Varaus selected = varausTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Valitse päivitettävä varaus!");
            return;
        }

        if (varausAsiakasCombo.getValue() == null ||
                varausMokkiCombo.getValue() == null ||
                varausTyontekijaCombo.getValue() == null ||
                varausAlkuPvm.getValue() == null ||
                varausLoppuPvm.getValue() == null) {

            showError("Kaikki kentät ovat pakollisia!");
            return;
        }

        int asiakasId = Integer.parseInt(varausAsiakasCombo.getValue().split(" - ")[0]);
        int mokkiId = Integer.parseInt(varausMokkiCombo.getValue().split(" - ")[0]);
        int tyontekijaId = Integer.parseInt(varausTyontekijaCombo.getValue().split(" - ")[0]);

        long days = java.time.temporal.ChronoUnit.DAYS.between(
                varausAlkuPvm.getValue(),
                varausLoppuPvm.getValue());

        if (days <= 0) {
            showError("Loppupäivän täytyy olla alkupäivän jälkeen!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            // Get updated cabin price
            double cabinPrice = 0;

            String priceQuery = "SELECT hinta FROM mokki WHERE mokki_id = ?";

            try (PreparedStatement priceStmt = conn.prepareStatement(priceQuery)) {

                priceStmt.setInt(1, mokkiId);

                ResultSet rs = priceStmt.executeQuery();

                if (rs.next()) {
                    cabinPrice = rs.getDouble("hinta");
                }
            }

            double totalPrice = cabinPrice * days;

            // Update reservation
            String updateQuery = "UPDATE varaus SET asiakas_id=?, mokki_id=?, tyontekija_id=?, alku_pvm=?, loppu_pvm=? WHERE varaus_id=?";

            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

                pstmt.setInt(1, asiakasId);
                pstmt.setInt(2, mokkiId);
                pstmt.setInt(3, tyontekijaId);
                pstmt.setDate(4, Date.valueOf(varausAlkuPvm.getValue()));
                pstmt.setDate(5, Date.valueOf(varausLoppuPvm.getValue()));
                pstmt.setInt(6, selected.getId());

                pstmt.executeUpdate();
            }

            // Update invoice automatically
            String updateInvoiceQuery = "UPDATE lasku SET summa=? WHERE varaus_id=?";

            try (PreparedStatement laskuStmt = conn.prepareStatement(updateInvoiceQuery)) {

                laskuStmt.setDouble(1, totalPrice);
                laskuStmt.setInt(2, selected.getId());

                laskuStmt.executeUpdate();
            }

            loadVaraukset();
            loadLaskut();
            clearVarausForm();

            showInfo("Varaus päivitetty onnistuneesti!");

        } catch (SQLException e) {
            showError("Virhe päivitettäessä varausta: " + e.getMessage());
        }
    }

    private void deleteVaraus() {
        Varaus selected = varausTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Valitse poistettava varaus taulukosta!");
            return;
        }

        String deleteLaskuQuery = "DELETE FROM lasku WHERE varaus_id=?";
        String deleteVarausQuery = "DELETE FROM varaus WHERE varaus_id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            // Delete invoice first
            try (PreparedStatement pstmtLasku = conn.prepareStatement(deleteLaskuQuery)) {
                pstmtLasku.setInt(1, selected.getId());
                pstmtLasku.executeUpdate();
            }

            // Then delete reservation
            try (PreparedStatement pstmtVaraus = conn.prepareStatement(deleteVarausQuery)) {
                pstmtVaraus.setInt(1, selected.getId());
                pstmtVaraus.executeUpdate();
            }

            loadVaraukset();
            loadLaskut();

            showInfo("Varaus ja siihen liittyvä lasku poistettu onnistuneesti!");

        } catch (SQLException e) {
            showError("Virhe poistettaessa varausta: " + e.getMessage());
        }
    }

    private void clearVarausForm() {
        varausAsiakasCombo.setValue(null);
        varausMokkiCombo.setValue(null);
        varausTyontekijaCombo.setValue(null);
        varausAlkuPvm.setValue(null);
        varausLoppuPvm.setValue(null);
    }

    // ============ LASKUT TAB ============

    private VBox buildLaskutTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        // === REPORTING FILTERS ===
        Label reportTitle = new Label("Raportointi");
        reportTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane filterGrid = new GridPane();
        filterGrid.setHgap(10);
        filterGrid.setVgap(10);
        filterGrid.setPadding(new Insets(10));
        filterGrid.setStyle("-fx-background-color: #e8f4f8; -fx-background-radius: 8px;");

        laskutAlkuPvm = new DatePicker();
        laskutLoppuPvm = new DatePicker();

        filterGrid.add(new Label("Alkaen:"), 1, 0);
        filterGrid.add(laskutAlkuPvm, 2, 0);
        filterGrid.add(new Label("Päättyen:"), 3, 0);
        filterGrid.add(laskutLoppuPvm, 4, 0);

        Button filterBtn = new Button("Suodata");
        Button clearFilterBtn = new Button("Tyhjennä suodatin");

        filterBtn.setOnAction(e -> loadLaskutWithFilter());
        clearFilterBtn.setOnAction(e -> {
            laskutAlkuPvm.setValue(null);
            laskutLoppuPvm.setValue(null);
            loadLaskut();
            updateReportSummary();
        });

        HBox filterButtons = new HBox(10);
        filterButtons.getChildren().addAll(filterBtn, clearFilterBtn);
        filterGrid.add(filterButtons, 5, 0);

        // === SUMMARY STATISTICS ===
        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(20);
        summaryGrid.setVgap(10);
        summaryGrid.setPadding(new Insets(15));
        summaryGrid.setStyle(
                "-fx-background-color: white; -fx-border-color: #e1e4e8; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        totalRevenueLabel = new Label("0.00 €");
        totalRevenueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        totalBookingsLabel = new Label("0");
        totalBookingsLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        popularCabinLabel = new Label("-");
        popularCabinLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        VBox revenueBox = new VBox(5);
        revenueBox.getChildren().addAll(new Label("Kokonaistulot"), totalRevenueLabel);

        VBox bookingsBox = new VBox(5);
        bookingsBox.getChildren().addAll(new Label("Varausten määrä"), totalBookingsLabel);

        VBox cabinBox = new VBox(5);
        cabinBox.getChildren().addAll(new Label("Suosituin mökki"), popularCabinLabel);

        summaryGrid.add(revenueBox, 0, 0);
        summaryGrid.add(bookingsBox, 1, 0);
        summaryGrid.add(cabinBox, 2, 0);

        // === TABLE ===
        laskuTable = new TableView<>();

        laskuTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        laskuTable.setItems(laskuData);

        TableColumn<Lasku, Integer> idCol = new TableColumn<>("Lasku ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);

        TableColumn<Lasku, String> asiakasCol = new TableColumn<>("Asiakas");
        asiakasCol.setCellValueFactory(new PropertyValueFactory<>("asiakasNimi"));
        asiakasCol.setPrefWidth(150);

        TableColumn<Lasku, String> mokkiCol = new TableColumn<>("Mökki");
        mokkiCol.setCellValueFactory(new PropertyValueFactory<>("mokkiNimi"));
        mokkiCol.setPrefWidth(120);

        TableColumn<Lasku, String> alkuCol = new TableColumn<>("Alkupäivä");
        alkuCol.setCellValueFactory(new PropertyValueFactory<>("alkuPvm"));
        alkuCol.setPrefWidth(100);

        TableColumn<Lasku, String> loppuCol = new TableColumn<>("Loppupäivä");
        loppuCol.setCellValueFactory(new PropertyValueFactory<>("loppuPvm"));
        loppuCol.setPrefWidth(100);

        TableColumn<Lasku, Double> summaCol = new TableColumn<>("Summa (€)");
        summaCol.setCellValueFactory(new PropertyValueFactory<>("summa"));
        summaCol.setPrefWidth(100);

        TableColumn<Lasku, String> erapaivaCol = new TableColumn<>("Eräpäivä");
        erapaivaCol.setCellValueFactory(new PropertyValueFactory<>("erapaiva"));
        erapaivaCol.setPrefWidth(100);

        laskuTable.getColumns().addAll(idCol, asiakasCol, mokkiCol, alkuCol, loppuCol, summaCol, erapaivaCol);

        // === BUTTONS ===
        Button refreshBtn = new Button("Päivitä laskut");
        Button deleteBtn = new Button("Poista lasku");

        refreshBtn.setOnAction(e -> {
            loadLaskut();
            updateReportSummary();
        });
        deleteBtn.setOnAction(e -> deleteLasku());

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(refreshBtn, deleteBtn);

        layout.getChildren().addAll(reportTitle, filterGrid, summaryGrid, new Label("Laskut"), laskuTable, buttonBox);
        return layout;
    }

    private void loadLaskutWithFilter() {
        if (laskutAlkuPvm.getValue() == null || laskutLoppuPvm.getValue() == null) {
            showError("Valitse sekä alku- että loppupäivämäärä!");
            return;
        }

        if (laskutAlkuPvm.getValue().isAfter(laskutLoppuPvm.getValue())) {
            showError("Alkupäivä ei voi olla loppupäivän jälkeen!");
            return;
        }

        laskuData.clear();
        String query = "SELECT l.lasku_id, l.summa, l.erapaiva, " +
                "a.etu_nimi, a.suku_nimi, " +
                "m.nimi AS mokki_nimi, " +
                "v.alku_pvm, v.loppu_pvm " +
                "FROM lasku l " +
                "JOIN varaus v ON l.varaus_id = v.varaus_id " +
                "JOIN asiakas a ON v.asiakas_id = a.asiakas_id " +
                "JOIN mokki m ON v.mokki_id = m.mokki_id " +
                "WHERE v.alku_pvm >= ? AND v.loppu_pvm <= ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDate(1, Date.valueOf(laskutAlkuPvm.getValue()));
            pstmt.setDate(2, Date.valueOf(laskutLoppuPvm.getValue()));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                laskuData.add(new Lasku(
                        rs.getInt("lasku_id"),
                        rs.getString("etu_nimi") + " " + rs.getString("suku_nimi"),
                        rs.getString("mokki_nimi"),
                        rs.getString("alku_pvm"),
                        rs.getString("loppu_pvm"),
                        rs.getDouble("summa"),
                        rs.getString("erapaiva")));
            }

            updateReportSummary();

        } catch (SQLException e) {
            showError("Virhe suodatettaessa laskuja: " + e.getMessage());
        }
    }

    private void updateReportSummary() {
        // Calculate total revenue
        double totalRevenue = 0;
        int totalBookings = laskuData.size();

        for (Lasku lasku : laskuData) {
            totalRevenue += lasku.getSumma();
        }

        totalRevenueLabel.setText(String.format("%.2f €", totalRevenue));
        totalBookingsLabel.setText(String.valueOf(totalBookings));

        // Find most popular cabin
        if (!laskuData.isEmpty()) {
            java.util.Map<String, Integer> cabinCounts = new java.util.HashMap<>();

            for (Lasku lasku : laskuData) {
                String cabin = lasku.getMokkiNimi();
                cabinCounts.put(cabin, cabinCounts.getOrDefault(cabin, 0) + 1);
            }

            String mostPopular = cabinCounts.entrySet().stream()
                    .max(java.util.Map.Entry.comparingByValue())
                    .map(java.util.Map.Entry::getKey)
                    .orElse("-");

            int count = cabinCounts.getOrDefault(mostPopular, 0);
            popularCabinLabel.setText(mostPopular + " (" + count + " varausta)");
        } else {
            popularCabinLabel.setText("-");
        }
    }

    private void loadLaskut() {
        laskuData.clear();
        String query = "SELECT l.lasku_id, l.summa, l.erapaiva, " +
                "a.etu_nimi, a.suku_nimi, " +
                "m.nimi AS mokki_nimi, " +
                "v.alku_pvm, v.loppu_pvm " +
                "FROM lasku l " +
                "JOIN varaus v ON l.varaus_id = v.varaus_id " +
                "JOIN asiakas a ON v.asiakas_id = a.asiakas_id " +
                "JOIN mokki m ON v.mokki_id = m.mokki_id";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                laskuData.add(new Lasku(
                        rs.getInt("lasku_id"),
                        rs.getString("etu_nimi") + " " + rs.getString("suku_nimi"),
                        rs.getString("mokki_nimi"),
                        rs.getString("alku_pvm"),
                        rs.getString("loppu_pvm"),
                        rs.getDouble("summa"),
                        rs.getString("erapaiva")));
            }

            updateReportSummary();

        } catch (SQLException e) {
            showError("Virhe ladattaessa laskuja: " + e.getMessage());
        }
    }

    private void deleteLasku() {
        Lasku selected = laskuTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Valitse poistettava lasku taulukosta!");
            return;
        }

        String query = "DELETE FROM lasku WHERE lasku_id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();

            loadLaskut();

            showInfo("Lasku poistettu onnistuneesti!");

        } catch (SQLException e) {
            showError("Virhe poistettaessa laskua: " + e.getMessage());
        }
    }

    // ============ UTILITY METHODS ============

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Virhe");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Onnistui");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ============ MODEL CLASSES ============

    public static class Asiakas {
        private int id;
        private String etuNimi;
        private String sukuNimi;
        private String puhNro;
        private String sposti;
        private String osoite;

        public Asiakas(int id, String etuNimi, String sukuNimi, String puhNro, String sposti, String osoite) {
            this.id = id;
            this.etuNimi = etuNimi;
            this.sukuNimi = sukuNimi;
            this.puhNro = puhNro;
            this.sposti = sposti;
            this.osoite = osoite;
        }

        public int getId() {
            return id;
        }

        public String getEtuNimi() {
            return etuNimi;
        }

        public String getSukuNimi() {
            return sukuNimi;
        }

        public String getPuhNro() {
            return puhNro;
        }

        public String getSposti() {
            return sposti;
        }

        public String getOsoite() {
            return osoite;
        }
    }

    public static class Mokki {
        private int id;
        private String nimi;
        private String sijainti;
        private int kapasiteetti;
        private double hinta;

        public Mokki(int id, String nimi, String sijainti, int kapasiteetti, double hinta) {
            this.id = id;
            this.nimi = nimi;
            this.sijainti = sijainti;
            this.kapasiteetti = kapasiteetti;
            this.hinta = hinta;
        }

        public int getId() {
            return id;
        }

        public String getNimi() {
            return nimi;
        }

        public String getSijainti() {
            return sijainti;
        }

        public int getKapasiteetti() {
            return kapasiteetti;
        }

        public double getHinta() {
            return hinta;
        }
    }

    public static class Tyontekija {
        private int id;
        private String etuNimi;
        private String sukuNimi;
        private String sposti;

        public Tyontekija(int id, String etuNimi, String sukuNimi, String sposti) {
            this.id = id;
            this.etuNimi = etuNimi;
            this.sukuNimi = sukuNimi;
            this.sposti = sposti;
        }

        public int getId() {
            return id;
        }

        public String getEtuNimi() {
            return etuNimi;
        }

        public String getSukuNimi() {
            return sukuNimi;
        }

        public String getSposti() {
            return sposti;
        }
    }

    public static class Varaus {
        private int id;
        private String asiakasNimi;
        private String mokkiNimi;
        private String tyontekijaNimi;
        private String alkuPvm;
        private String loppuPvm;

        public Varaus(int id, String asiakasNimi, String mokkiNimi, String tyontekijaNimi, String alkuPvm,
                String loppuPvm) {
            this.id = id;
            this.asiakasNimi = asiakasNimi;
            this.mokkiNimi = mokkiNimi;
            this.tyontekijaNimi = tyontekijaNimi;
            this.alkuPvm = alkuPvm;
            this.loppuPvm = loppuPvm;
        }

        public int getId() {
            return id;
        }

        public String getAsiakasNimi() {
            return asiakasNimi;
        }

        public String getMokkiNimi() {
            return mokkiNimi;
        }

        public String getTyontekijaNimi() {
            return tyontekijaNimi;
        }

        public String getAlkuPvm() {
            return alkuPvm;
        }

        public String getLoppuPvm() {
            return loppuPvm;
        }
    }

    public static class Lasku {
        private int id;
        private String asiakasNimi;
        private String mokkiNimi;
        private String alkuPvm;
        private String loppuPvm;
        private double summa;
        private String erapaiva;

        public Lasku(int id, String asiakasNimi, String mokkiNimi, String alkuPvm, String loppuPvm, double summa,
                String erapaiva) {
            this.id = id;
            this.asiakasNimi = asiakasNimi;
            this.mokkiNimi = mokkiNimi;
            this.alkuPvm = alkuPvm;
            this.loppuPvm = loppuPvm;
            this.summa = summa;
            this.erapaiva = erapaiva;
        }

        public int getId() {
            return id;
        }

        public String getAsiakasNimi() {
            return asiakasNimi;
        }

        public String getMokkiNimi() {
            return mokkiNimi;
        }

        public String getAlkuPvm() {
            return alkuPvm;
        }

        public String getLoppuPvm() {
            return loppuPvm;
        }

        public double getSumma() {
            return summa;
        }

        public String getErapaiva() {
            return erapaiva;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}