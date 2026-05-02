package com.insuretechpro.ui;

import com.insuretechpro.exception.RepositoryException;
import com.insuretechpro.model.Agent;
import com.insuretechpro.model.Claim;
import com.insuretechpro.model.Customer;
import com.insuretechpro.model.DashboardStats;
import com.insuretechpro.model.HealthPolicy;
import com.insuretechpro.model.LifePolicy;
import com.insuretechpro.model.Payment;
import com.insuretechpro.model.Policy;
import com.insuretechpro.model.PolicyDistribution;
import com.insuretechpro.model.RecentClaimView;
import com.insuretechpro.model.User;
import com.insuretechpro.model.VehiclePolicy;
import com.insuretechpro.repository.AgentRepository;
import com.insuretechpro.repository.ClaimRepository;
import com.insuretechpro.repository.CustomerRepository;
import com.insuretechpro.repository.DashboardRepository;
import com.insuretechpro.repository.PaymentRepository;
import com.insuretechpro.repository.PolicyRepository;
import com.insuretechpro.repository.UserRepository;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * InsureTechProApp is the final JavaFX GUI.
 * It uses the same repositories as the console app, so data is stored in MySQL.
 */
public class InsureTechProApp extends Application {
    private final UserRepository userRepository = new UserRepository();
    private final DashboardRepository dashboardRepository = new DashboardRepository();
    private final AgentRepository agentRepository = new AgentRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final PolicyRepository policyRepository = new PolicyRepository();
    private final ClaimRepository claimRepository = new ClaimRepository();
    private final PaymentRepository paymentRepository = new PaymentRepository();

    private Stage stage;
    private BorderPane appRoot;
    private User loggedInUser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // JavaFX starts here and shows the login page first.
        this.stage = primaryStage;
        this.stage.setTitle("InsureTech Pro v1.0");
        showLogin();
    }

    private void showLogin() {
        Label brand = new Label("INSURETECH PRO");
        brand.getStyleClass().add("sidebar-title");

        Label title = new Label("Role Login");
        title.getStyleClass().add("page-title");

        TextField usernameField = new TextField("admin");
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setText("admin123");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Label hint = new Label("Admin: admin/admin123 | Agent: agent/agent123 | Customer: customer/customer123");
        hint.getStyleClass().add("muted-label");

        VBox panel = new VBox(14, brand, title, usernameField, passwordField, loginButton, hint);
        panel.getStyleClass().add("login-panel");
        panel.setPadding(new Insets(28));
        panel.setMaxWidth(360);
        panel.setAlignment(Pos.CENTER_LEFT);

        BorderPane root = new BorderPane(panel);
        root.getStyleClass().add("login-root");
        BorderPane.setAlignment(panel, Pos.CENTER);

        loginButton.setOnAction(event -> login(usernameField.getText(), passwordField.getText()));
        passwordField.setOnAction(event -> login(usernameField.getText(), passwordField.getText()));

        Scene scene = new Scene(root, 980, 620);
        applyStyle(scene);
        stage.setScene(scene);
        stage.show();
    }

    private void login(String username, String password) {
        try {
            // UserRepository checks credentials from the users table.
            loggedInUser = userRepository.authenticate(username.trim(), password);
            showMainApp();
        } catch (RepositoryException exception) {
            showError(exception.getMessage());
        }
    }

    private void showMainApp() {
        appRoot = new BorderPane();
        appRoot.getStyleClass().add("app-root");
        appRoot.setLeft(createSidebar());
        appRoot.setTop(createTopBar());
        showDashboard();

        Scene scene = new Scene(appRoot, 1180, 720);
        applyStyle(scene);
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    private VBox createSidebar() {
        Label brand = new Label("INSURETECH PRO");
        brand.getStyleClass().add("sidebar-title");

        VBox sidebar = new VBox(10, brand);
        sidebar.getChildren().add(navButton("Dashboard", this::showDashboard));

        if (canViewAgents()) {
            sidebar.getChildren().add(navButton("Agents", this::showAgents));
        }

        if (isAdmin()) {
            sidebar.getChildren().add(navButton("Users", this::showUsers));
        }

        if (canViewCustomers()) {
            sidebar.getChildren().add(navButton("Customers", this::showCustomers));
        }

        sidebar.getChildren().addAll(
                navButton("Policies", this::showPolicies),
                navButton("Claims", this::showClaims),
                navButton("Payments", this::showPayments)
        );

        if (isAdmin()) {
            sidebar.getChildren().addAll(
                    navButton("Reports", this::showReports),
                    navButton("Settings", () -> showPlaceholder("Settings"))
            );
        }

        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 12, 12, 12));
        sidebar.setPrefWidth(220);
        return sidebar;
    }

    private Button navButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> action.run());
        return button;
    }

    private HBox createTopBar() {
        Label welcome = new Label("Welcome, " + loggedInUser.getRole() + " (" + loggedInUser.getFullName() + ")");
        welcome.getStyleClass().add("muted-label");

        Button logout = new Button("Logout");
        logout.getStyleClass().add("primary-button");
        logout.setOnAction(event -> showLogin());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(16, spacer, welcome, logout);
        topBar.getStyleClass().add("top-bar");
        topBar.setPadding(new Insets(14, 18, 14, 18));
        topBar.setAlignment(Pos.CENTER_RIGHT);
        return topBar;
    }

    private void showDashboard() {
        Label title = pageTitle("Operations Dashboard");
        Label subtitle = new Label("Live role-based view for " + loggedInUser.getRole() + " - " + loggedInUser.getFullName());
        subtitle.getStyleClass().add("muted-label");

        TableView<RecentClaimView> recentClaimsTable = createRecentClaimsTable();
        PieChart policyChart = new PieChart();
        policyChart.setTitle("Policy Distribution");

        HBox topStats = new HBox(16);
        HBox bottomStats = new HBox(16);
        Label insight = new Label();
        insight.getStyleClass().add("muted-label");

        try {
            DashboardStats stats = dashboardStatsForCurrentRole();
            List<Customer> customers = visibleCustomers();
            List<Policy> policies = visiblePolicies();
            List<Claim> claims = visibleClaims();
            List<Payment> payments = visiblePayments();

            topStats.getChildren().addAll(
                    statCard("Customers", String.valueOf(customers.size()), "Active relationships"),
                    statCard("Policies", String.valueOf(stats.getActivePolicies()), "Currently active"),
                    statCard("Pending Claims", String.valueOf(stats.getPendingClaims()), "Need attention"),
                    statCard("Monthly Revenue", String.format("$%.0f", stats.getMonthlyRevenue()), "Paid premiums")
            );

            bottomStats.getChildren().addAll(
                    statCard("Total Claims", String.valueOf(claims.size()), "Visible to this role"),
                    statCard("Payments", String.valueOf(payments.size()), "All visible transactions"),
                    statCard("Paid Amount", String.format("$%.0f", totalPaid(payments)), "Collected premium"),
                    statCard("Role", loggedInUser.getRole(), "Access mode")
            );

            insight.setText("Showing " + policies.size() + " policies, " + claims.size()
                    + " claims, and " + payments.size() + " payments for this login.");

            recentClaimsTable.setItems(FXCollections.observableArrayList(recentClaimsForCurrentRole(12)));

            for (PolicyDistribution item : policyDistributionForCurrentRole()) {
                policyChart.getData().add(new PieChart.Data(item.getPolicyType(), item.getTotalCount()));
            }
        } catch (RepositoryException exception) {
            showError(exception.getMessage());
        }

        VBox recentCard = cardBox("Recent Claim Activity", recentClaimsTable);
        VBox chartCard = cardBox("Policy Distribution", policyChart);
        HBox lower = new HBox(18, recentCard, chartCard);
        HBox.setHgrow(recentCard, Priority.ALWAYS);
        chartCard.setPrefWidth(360);

        VBox hero = new VBox(6, title, subtitle, insight);
        hero.getStyleClass().add("hero-panel");

        VBox content = new VBox(18, hero, topStats, bottomStats, lower);
        content.setPadding(new Insets(22));
        appRoot.setCenter(content);
    }

    private VBox statCard(String title, String value, String detail) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("card-number");

        Label detailLabel = new Label(detail);
        detailLabel.getStyleClass().add("muted-label");

        VBox card = new VBox(8, titleLabel, valueLabel, detailLabel);
        card.getStyleClass().add("card");
        card.setPrefHeight(128);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox cardBox(String title, javafx.scene.Node content) {
        Label label = new Label(title);
        label.getStyleClass().add("card-title");
        VBox box = new VBox(12, label, content);
        box.getStyleClass().add("card");
        return box;
    }

    private void showAgents() {
        if (!canViewAgents()) {
            showError("Only admin can manage agents.");
            showDashboard();
            return;
        }

        TableView<Agent> table = new TableView<>();
        addLongColumn(table, "ID", Agent::getAgentId);
        addTextColumn(table, "Code", Agent::getAgentCode);
        addTextColumn(table, "Name", Agent::getFullName);
        addTextColumn(table, "Email", Agent::getEmail);
        addTextColumn(table, "Phone", Agent::getPhone);
        addDoubleColumn(table, "Commission", Agent::getCommissionRate);
        addTextColumn(table, "Status", Agent::getStatus);

        HBox actions = new HBox(10,
                actionButton("Add Agent", () -> showAgentDialog(table)),
                actionButton("Activate", () -> updateSelectedAgentStatus(table, "ACTIVE")),
                dangerButton("Deactivate", () -> updateSelectedAgentStatus(table, "INACTIVE")),
                dangerButton("Delete", () -> deleteSelectedAgent(table))
        );
        loadAgents(table);
        appRoot.setCenter(tablePage("Agents", actions, table));
    }

    private void showUsers() {
        if (!isAdmin()) {
            showError("Only admin can manage login users.");
            showDashboard();
            return;
        }

        TableView<User> table = new TableView<>();
        addLongColumn(table, "ID", User::getUserId);
        addTextColumn(table, "Username", User::getUsername);
        addTextColumn(table, "Full Name", User::getFullName);
        addTextColumn(table, "Role", User::getRole);
        addTextColumn(table, "Reference ID", user -> user.getReferenceId() == null ? "ADMIN" : String.valueOf(user.getReferenceId()));
        addTextColumn(table, "Status", User::getStatus);

        HBox actions = new HBox(10,
                actionButton("Add User", () -> showUserDialog(table)),
                actionButton("Activate", () -> updateSelectedUserStatus(table, "ACTIVE")),
                dangerButton("Deactivate", () -> updateSelectedUserStatus(table, "INACTIVE")),
                dangerButton("Delete", () -> deleteSelectedUser(table))
        );
        loadUsers(table);
        appRoot.setCenter(tablePage("Users", actions, table));
    }

    private void showCustomers() {
        if (!canViewCustomers()) {
            showError("You do not have permission to view customers.");
            showDashboard();
            return;
        }

        TableView<Customer> table = new TableView<>();
        addLongColumn(table, "ID", Customer::getCustomerId);
        addTextColumn(table, "Code", Customer::getCustomerCode);
        addLongColumn(table, "Agent ID", Customer::getAgentId);
        addTextColumn(table, "Name", Customer::getFullName);
        addTextColumn(table, "Email", Customer::getEmail);
        addTextColumn(table, "Phone", Customer::getPhone);
        addTextColumn(table, "City", Customer::getCity);
        addTextColumn(table, "Status", Customer::getStatus);

        HBox actions = new HBox(10,
                actionButton("Add Customer", () -> showCustomerDialog(table)),
                actionButton("Activate", () -> updateSelectedCustomerStatus(table, "ACTIVE")),
                dangerButton("Deactivate", () -> updateSelectedCustomerStatus(table, "INACTIVE")),
                dangerButton("Delete", () -> deleteSelectedCustomer(table))
        );
        loadCustomers(table);
        appRoot.setCenter(tablePage("Customers", actions, table));
    }

    private void showPolicies() {
        TableView<Policy> table = new TableView<>();
        addLongColumn(table, "ID", Policy::getPolicyId);
        addTextColumn(table, "Number", Policy::getPolicyNumber);
        addLongColumn(table, "Customer ID", Policy::getCustomerId);
        addTextColumn(table, "Name", Policy::getPolicyName);
        addDoubleColumn(table, "Premium", Policy::getPremiumAmount);
        addDoubleColumn(table, "Coverage", Policy::getCoverageAmount);
        addTextColumn(table, "Status", Policy::getStatus);

        HBox actions = new HBox(10);
        if (canCreatePolicy()) {
            actions.getChildren().add(actionButton("Add Policy", () -> showPolicyDialog(table)));
        }
        if (isAdmin() || isAgent()) {
            actions.getChildren().add(actionButton("Activate", () -> updateSelectedPolicyStatus(table, "ACTIVE")));
            actions.getChildren().add(dangerButton("Cancel", () -> updateSelectedPolicyStatus(table, "CANCELLED")));
        }
        if (isAdmin()) {
            actions.getChildren().add(dangerButton("Delete", () -> deleteSelectedPolicy(table)));
        }
        loadPolicies(table);
        appRoot.setCenter(tablePage("Policies", actions, table));
    }

    private void showClaims() {
        TableView<Claim> table = new TableView<>();
        addLongColumn(table, "ID", Claim::getClaimId);
        addTextColumn(table, "Number", Claim::getClaimNumber);
        addLongColumn(table, "Policy ID", Claim::getPolicyId);
        addLongColumn(table, "Customer ID", Claim::getCustomerId);
        addDoubleColumn(table, "Claim Amount", Claim::getClaimAmount);
        addDoubleColumn(table, "Approved", Claim::getApprovedAmount);
        addTextColumn(table, "Status", Claim::getStatus);

        HBox actions = new HBox(10);
        if (canSubmitClaim()) {
            actions.getChildren().add(actionButton("Submit Claim", () -> showClaimDialog(table)));
        }
        if (isAgent() || isAdmin()) {
            actions.getChildren().add(actionButton("Process", () -> processSelectedClaim(table)));
            actions.getChildren().add(actionButton("Reject", () -> rejectSelectedClaim(table)));
        }
        if (isAdmin()) {
            actions.getChildren().add(actionButton("Approve", () -> approveSelectedClaim(table)));
            actions.getChildren().add(actionButton("Settle", () -> settleSelectedClaim(table)));
        }
        if (isCustomer() || isAdmin()) {
            actions.getChildren().add(dangerButton("Cancel", () -> cancelSelectedClaim(table)));
        }
        if (isAdmin()) {
            actions.getChildren().add(dangerButton("Delete", () -> deleteSelectedClaim(table)));
        }
        loadClaims(table);
        appRoot.setCenter(tablePage("Claims", actions, table));
    }

    private void showPayments() {
        TableView<Payment> table = new TableView<>();
        addLongColumn(table, "ID", Payment::getPaymentId);
        addTextColumn(table, "Reference", Payment::getPaymentReference);
        addLongColumn(table, "Policy ID", Payment::getPolicyId);
        addLongColumn(table, "Customer ID", Payment::getCustomerId);
        addDoubleColumn(table, "Amount", Payment::getAmount);
        addTextColumn(table, "Method", Payment::getPaymentMethod);
        addTextColumn(table, "Status", Payment::getStatus);

        HBox actions = new HBox(10, actionButton("Record Payment", () -> showPaymentDialog(table)));
        if (isAdmin()) {
            actions.getChildren().add(dangerButton("Delete", () -> deleteSelectedPayment(table)));
        }
        loadPayments(table);
        appRoot.setCenter(tablePage("Payments", actions, table));
    }

    private VBox tablePage(String title, javafx.scene.Node actionNode, TableView<?> table) {
        HBox header = new HBox(16, pageTitle(title), new Region(), actionNode);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(18, header, table);
        box.setPadding(new Insets(22));
        VBox.setVgrow(table, Priority.ALWAYS);
        return box;
    }

    private void showPlaceholder(String title) {
        Label heading = pageTitle(title);
        Label message = new Label(title + " screen is ready for future expansion.");
        message.getStyleClass().add("muted-label");
        VBox box = new VBox(12, heading, cardBox(title, message));
        box.setPadding(new Insets(22));
        appRoot.setCenter(box);
    }

    private void showReports() {
        try {
            List<Customer> customers = visibleCustomers();
            List<Policy> policies = visiblePolicies();
            List<Claim> claims = visibleClaims();
            List<Payment> payments = visiblePayments();

            VBox report = new VBox(14,
                    pageTitle("Management Reports"),
                    cardBox("Portfolio Summary", new Label("Customers: " + customers.size()
                            + " | Policies: " + policies.size()
                            + " | Claims: " + claims.size()
                            + " | Payments: " + payments.size())),
                    cardBox("Financial Summary", new Label("Paid premium collected: $" + String.format("%.2f", totalPaid(payments)))),
                    cardBox("Operational Summary", new Label("Pending claims: " + countClaimsByStatus(claims, "REQUESTED")
                            + " requested, " + countClaimsByStatus(claims, "PROCESSING") + " processing"))
            );
            report.setPadding(new Insets(22));
            appRoot.setCenter(report);
        } catch (RepositoryException exception) {
            showError(exception.getMessage());
        }
    }

    private void showAgentDialog(TableView<Agent> table) {
        Dialog<Agent> dialog = dialog("Add Agent");
        GridPane grid = formGrid();
        TextField code = field("AGT-1001");
        TextField name = field("Full name");
        TextField email = field("Email");
        TextField phone = field("Phone");
        DatePicker hireDate = new DatePicker(LocalDate.now());
        TextField commission = field("5.5");
        addRows(grid, new String[]{"Code", "Name", "Email", "Phone", "Hire Date", "Commission"},
                code, name, email, phone, hireDate, commission);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new Agent(0, code.getText(), name.getText(), email.getText(), phone.getText(),
                hireDate.getValue(), parseDouble(commission.getText()), "ACTIVE") : null);
        dialog.showAndWait().ifPresent(agent -> save(() -> agentRepository.saveAgent(agent), () -> loadAgents(table)));
    }

    private void showUserDialog(TableView<User> table) {
        Dialog<UserLoginRequest> dialog = dialog("Add Login User");
        GridPane grid = formGrid();
        TextField username = field("username");
        PasswordField password = new PasswordField();
        password.setPromptText("password");
        TextField name = field("Full name");
        ComboBox<String> role = combo("ADMIN", "AGENT", "CUSTOMER");
        TextField referenceId = field("Agent ID or Customer ID");
        addRows(grid, new String[]{"Username", "Password", "Full Name", "Role", "Reference ID"},
                username, password, name, role, referenceId);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> {
            if (button != ButtonType.OK) {
                return null;
            }

            String selectedRole = role.getValue();
            Long selectedReferenceId = "ADMIN".equals(selectedRole) ? null : parseLong(referenceId.getText());
            return new UserLoginRequest(username.getText(), password.getText(), name.getText(), selectedReferenceId, selectedRole);
        });
        dialog.showAndWait().ifPresent(request -> save(() -> {
            validateUserLoginRequest(request);
            return userRepository.saveUser(
                    request.username(),
                    request.password(),
                    request.fullName(),
                    request.referenceId(),
                    request.role()
            );
        }, () -> loadUsers(table)));
    }

    private void showCustomerDialog(TableView<Customer> table) {
        Dialog<Customer> dialog = dialog("Add Customer");
        GridPane grid = formGrid();
        TextField code = field("CUS-1001");
        TextField agentId = field("Agent ID");
        if (isAgent()) {
            // Agent can create customers only under their own agent ID.
            agentId.setText(String.valueOf(currentReferenceId()));
            agentId.setDisable(true);
        }
        TextField name = field("Full name");
        DatePicker dob = new DatePicker(LocalDate.of(2000, 1, 1));
        ComboBox<String> gender = combo("MALE", "FEMALE", "OTHER");
        TextField email = field("Email");
        TextField phone = field("Phone");
        TextField city = field("City");
        addRows(grid, new String[]{"Code", "Agent ID", "Name", "DOB", "Gender", "Email", "Phone", "City"},
                code, agentId, name, dob, gender, email, phone, city);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new Customer(0, code.getText(), parseLong(agentId.getText()), name.getText(), dob.getValue(),
                gender.getValue(), email.getText(), phone.getText(), "", city.getText(), "", "", "ACTIVE") : null);
        dialog.showAndWait().ifPresent(customer -> save(() -> {
            validateCustomerBeforeSave(customer);
            return customerRepository.saveCustomer(customer);
        }, () -> loadCustomers(table)));
    }

    private void showPolicyDialog(TableView<Policy> table) {
        Dialog<Policy> dialog = dialog("Add Policy");
        GridPane grid = formGrid();
        ComboBox<String> type = combo("LIFE", "HEALTH", "VEHICLE");
        TextField number = field("POL-1001");
        TextField customerId = field("Customer ID");
        TextField agentId = field("Agent ID");
        if (isAgent()) {
            // Agent-created policies are assigned to the logged-in agent.
            agentId.setText(String.valueOf(currentReferenceId()));
            agentId.setDisable(true);
        }
        TextField name = field("Policy name");
        TextField coverage = field("500000");
        ComboBox<String> frequency = combo("MONTHLY", "QUARTERLY", "YEARLY");
        DatePicker start = new DatePicker(LocalDate.now());
        DatePicker end = new DatePicker(LocalDate.now().plusYears(1));
        TextField extraOne = field("Nominee / Members / Vehicle No");
        TextField extraTwo = field("Relation / Plan / Vehicle Type");
        addRows(grid, new String[]{"Type", "Number", "Customer ID", "Agent ID", "Name", "Coverage",
                        "Frequency", "Start", "End", "Extra 1", "Extra 2"},
                type, number, customerId, agentId, name, coverage, frequency, start, end, extraOne, extraTwo);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? buildPolicy(type.getValue(), number.getText(), customerId.getText(), agentId.getText(),
                name.getText(), coverage.getText(), frequency.getValue(), start.getValue(), end.getValue(),
                extraOne.getText(), extraTwo.getText()) : null);
        dialog.showAndWait().ifPresent(policy -> save(() -> {
            validatePolicyBeforeSave(policy);
            policy.setPremiumAmount(policy.calculatePremium());
            return policyRepository.savePolicy(policy);
        }, () -> loadPolicies(table)));
    }

    private Policy buildPolicy(String type, String number, String customerId, String agentId, String name,
                               String coverage, String frequency, LocalDate start, LocalDate end,
                               String extraOne, String extraTwo) {
        long customer = parseLong(customerId);
        long agent = parseLong(agentId);
        double cover = parseDouble(coverage);

        if ("LIFE".equals(type)) {
            return new LifePolicy(0, number, customer, agent, name, 0, cover, frequency, start, end,
                    "ACTIVE", extraOne, extraTwo, 30, "None", "LOW");
        } else if ("HEALTH".equals(type)) {
            return new HealthPolicy(0, number, customer, agent, name, 0, cover, frequency, start, end,
                    "ACTIVE", parseInt(extraOne, 1), "None", extraTwo, 5000);
        }

        return new VehiclePolicy(0, number, customer, agent, name, 0, cover, frequency, start, end,
                "ACTIVE", extraOne, extraTwo, "Unknown", "Unknown", LocalDate.now().getYear(),
                number + "-ENG", number + "-CHS");
    }

    private void showClaimDialog(TableView<Claim> table) {
        Dialog<Claim> dialog = dialog("Submit Claim");
        GridPane grid = formGrid();
        TextField number = field("CLM-1001");
        TextField policyId = field("Policy ID");
        TextField customerId = field("Customer ID");
        if (isCustomer()) {
            // Customer can submit claims only for their own customer ID.
            customerId.setText(String.valueOf(currentReferenceId()));
            customerId.setDisable(true);
        }
        DatePicker date = new DatePicker(LocalDate.now());
        TextField amount = field("75000");
        TextField reason = field("Reason");
        addRows(grid, new String[]{"Number", "Policy ID", "Customer ID", "Date", "Amount", "Reason"},
                number, policyId, customerId, date, amount, reason);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new Claim(0, number.getText(), parseLong(policyId.getText()), parseLong(customerId.getText()),
                date.getValue(), parseDouble(amount.getText()), 0, reason.getText(), "REQUESTED", "Claim submitted") : null);
        dialog.showAndWait().ifPresent(claim -> save(() -> {
            validateClaimBeforeSave(claim);
            return claimRepository.saveClaim(claim);
        }, () -> loadClaims(table)));
    }

    private void showPaymentDialog(TableView<Payment> table) {
        Dialog<Payment> dialog = dialog("Record Payment");
        GridPane grid = formGrid();
        TextField reference = field("PAY-1001");
        TextField policyId = field("Policy ID");
        TextField customerId = field("Customer ID");
        if (isCustomer()) {
            // Customer can record payments only under their own customer ID.
            customerId.setText(String.valueOf(currentReferenceId()));
            customerId.setDisable(true);
        }
        DatePicker date = new DatePicker(LocalDate.now());
        TextField amount = field("11500");
        ComboBox<String> method = combo("CASH", "CARD", "UPI", "BANK_TRANSFER");
        ComboBox<String> status = combo("PAID", "FAILED", "PENDING");
        addRows(grid, new String[]{"Reference", "Policy ID", "Customer ID", "Date", "Amount", "Method", "Status"},
                reference, policyId, customerId, date, amount, method, status);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new Payment(0, reference.getText(), parseLong(policyId.getText()), parseLong(customerId.getText()),
                date.getValue(), parseDouble(amount.getText()), method.getValue(), status.getValue()) : null);
        dialog.showAndWait().ifPresent(payment -> save(() -> {
            validatePaymentBeforeSave(payment);
            return paymentRepository.savePayment(payment);
        }, () -> loadPayments(table)));
    }

    private void approveSelectedClaim(TableView<Claim> table) {
        Claim claim = table.getSelectionModel().getSelectedItem();
        if (!canWorkOnClaim(claim)) {
            return;
        }

        claim.approveClaim(claim.getClaimAmount(), "Approved from JavaFX app");
        save(() -> {
            claimRepository.updateClaimStatus(claim);
            return claim.getClaimId();
        }, () -> loadClaims(table));
    }

    private void processSelectedClaim(TableView<Claim> table) {
        Claim claim = table.getSelectionModel().getSelectedItem();
        if (!canWorkOnClaim(claim)) {
            return;
        }

        claim.processClaim("Documents are under review by " + loggedInUser.getFullName());
        save(() -> {
            claimRepository.updateClaimStatus(claim);
            return claim.getClaimId();
        }, () -> loadClaims(table));
    }

    private void rejectSelectedClaim(TableView<Claim> table) {
        Claim claim = table.getSelectionModel().getSelectedItem();
        if (!canWorkOnClaim(claim)) {
            return;
        }

        claim.rejectClaim("Rejected by " + loggedInUser.getRole());
        save(() -> {
            claimRepository.updateClaimStatus(claim);
            return claim.getClaimId();
        }, () -> loadClaims(table));
    }

    private void cancelSelectedClaim(TableView<Claim> table) {
        Claim claim = table.getSelectionModel().getSelectedItem();
        if (!canWorkOnClaim(claim)) {
            return;
        }

        claim.cancelClaim("Cancelled by " + loggedInUser.getRole());
        save(() -> {
            claimRepository.updateClaimStatus(claim);
            return claim.getClaimId();
        }, () -> loadClaims(table));
    }

    private void settleSelectedClaim(TableView<Claim> table) {
        Claim claim = table.getSelectionModel().getSelectedItem();
        if (!canWorkOnClaim(claim)) {
            return;
        }

        claim.setStatus("SETTLED");
        claim.setRemarks("Settled by admin after payment.");
        save(() -> {
            claimRepository.updateClaimStatus(claim);
            return claim.getClaimId();
        }, () -> loadClaims(table));
    }

    private void updateSelectedAgentStatus(TableView<Agent> table, String status) {
        Agent agent = table.getSelectionModel().getSelectedItem();
        if (agent == null) {
            showError("Please select an agent first.");
            return;
        }

        save(() -> {
            agentRepository.updateAgentStatus(agent.getAgentId(), status);
            return agent.getAgentId();
        }, () -> loadAgents(table));
    }

    private void deleteSelectedAgent(TableView<Agent> table) {
        Agent agent = table.getSelectionModel().getSelectedItem();
        if (agent == null) {
            showError("Please select an agent first.");
            return;
        }

        save(() -> {
            agentRepository.deleteAgent(agent.getAgentId());
            return agent.getAgentId();
        }, () -> loadAgents(table));
    }

    private void updateSelectedCustomerStatus(TableView<Customer> table, String status) {
        Customer customer = table.getSelectionModel().getSelectedItem();
        if (customer == null) {
            showError("Please select a customer first.");
            return;
        }
        if (!canAccessCustomer(customer)) {
            showError("This customer is not assigned to your login.");
            return;
        }

        save(() -> {
            customerRepository.updateCustomerStatus(customer.getCustomerId(), status);
            return customer.getCustomerId();
        }, () -> loadCustomers(table));
    }

    private void deleteSelectedCustomer(TableView<Customer> table) {
        Customer customer = table.getSelectionModel().getSelectedItem();
        if (customer == null) {
            showError("Please select a customer first.");
            return;
        }
        if (!canAccessCustomer(customer)) {
            showError("This customer is not assigned to your login.");
            return;
        }

        save(() -> {
            customerRepository.deleteCustomer(customer.getCustomerId());
            return customer.getCustomerId();
        }, () -> loadCustomers(table));
    }

    private void updateSelectedPolicyStatus(TableView<Policy> table, String status) {
        Policy policy = table.getSelectionModel().getSelectedItem();
        if (!canWorkOnPolicy(policy)) {
            return;
        }

        save(() -> {
            policyRepository.updatePolicyStatus(policy.getPolicyId(), status);
            return policy.getPolicyId();
        }, () -> loadPolicies(table));
    }

    private void deleteSelectedPolicy(TableView<Policy> table) {
        Policy policy = table.getSelectionModel().getSelectedItem();
        if (!canWorkOnPolicy(policy)) {
            return;
        }

        save(() -> {
            policyRepository.deletePolicyWithLinkedRecords(policy.getPolicyId());
            return policy.getPolicyId();
        }, () -> loadPolicies(table));
    }

    private void deleteSelectedClaim(TableView<Claim> table) {
        Claim claim = table.getSelectionModel().getSelectedItem();
        if (!canWorkOnClaim(claim)) {
            return;
        }

        save(() -> {
            claimRepository.deleteClaim(claim.getClaimId());
            return claim.getClaimId();
        }, () -> loadClaims(table));
    }

    private void deleteSelectedPayment(TableView<Payment> table) {
        Payment payment = table.getSelectionModel().getSelectedItem();
        if (payment == null) {
            showError("Please select a payment first.");
            return;
        }

        save(() -> {
            paymentRepository.deletePayment(payment.getPaymentId());
            return payment.getPaymentId();
        }, () -> loadPayments(table));
    }

    private void loadAgents(TableView<Agent> table) {
        load(() -> table.setItems(FXCollections.observableArrayList(agentRepository.findAllAgents())));
    }

    private void loadUsers(TableView<User> table) {
        load(() -> table.setItems(FXCollections.observableArrayList(userRepository.findAllUsers())));
    }

    private void loadCustomers(TableView<Customer> table) {
        load(() -> table.setItems(FXCollections.observableArrayList(visibleCustomers())));
    }

    private void loadPolicies(TableView<Policy> table) {
        load(() -> table.setItems(FXCollections.observableArrayList(visiblePolicies())));
    }

    private void loadClaims(TableView<Claim> table) {
        load(() -> table.setItems(FXCollections.observableArrayList(visibleClaims())));
    }

    private void loadPayments(TableView<Payment> table) {
        load(() -> table.setItems(FXCollections.observableArrayList(visiblePayments())));
    }

    private List<Customer> visibleCustomers() throws RepositoryException {
        List<Customer> customers = customerRepository.findAllCustomers();
        if (isAdmin()) {
            return customers;
        }

        List<Customer> visible = new ArrayList<>();
        for (Customer customer : customers) {
            if (isAgent() && customer.getAgentId() == currentReferenceId()) {
                visible.add(customer);
            } else if (isCustomer() && customer.getCustomerId() == currentReferenceId()) {
                visible.add(customer);
            }
        }
        return visible;
    }

    private List<Policy> visiblePolicies() throws RepositoryException {
        List<Policy> policies = policyRepository.findAllPolicies();
        if (isAdmin()) {
            return policies;
        }

        List<Policy> visible = new ArrayList<>();
        for (Policy policy : policies) {
            if (isAgent() && policy.getAgentId() == currentReferenceId()) {
                visible.add(policy);
            } else if (isCustomer() && policy.getCustomerId() == currentReferenceId()) {
                visible.add(policy);
            }
        }
        return visible;
    }

    private List<Claim> visibleClaims() throws RepositoryException {
        List<Claim> claims = claimRepository.findAllClaims();
        if (isAdmin()) {
            return claims;
        }

        List<Claim> visible = new ArrayList<>();
        for (Claim claim : claims) {
            if (canAccessClaim(claim)) {
                visible.add(claim);
            }
        }
        return visible;
    }

    private List<Payment> visiblePayments() throws RepositoryException {
        List<Payment> payments = paymentRepository.findAllPayments();
        if (isAdmin()) {
            return payments;
        }

        List<Payment> visible = new ArrayList<>();
        for (Payment payment : payments) {
            if (isCustomer() && payment.getCustomerId() == currentReferenceId()) {
                visible.add(payment);
            } else if (isAgent() && isPolicyAssignedToAgent(payment.getPolicyId())) {
                visible.add(payment);
            }
        }
        return visible;
    }

    private DashboardStats dashboardStatsForCurrentRole() throws RepositoryException {
        if (isAdmin()) {
            return dashboardRepository.getDashboardStats();
        }

        long activePolicies = 0;
        for (Policy policy : visiblePolicies()) {
            if ("ACTIVE".equals(policy.getStatus())) {
                activePolicies++;
            }
        }

        long pendingClaims = 0;
        for (Claim claim : visibleClaims()) {
            if ("REQUESTED".equals(claim.getStatus()) || "PROCESSING".equals(claim.getStatus())) {
                pendingClaims++;
            }
        }

        double monthlyRevenue = 0;
        LocalDate today = LocalDate.now();
        for (Payment payment : visiblePayments()) {
            if ("PAID".equals(payment.getStatus())
                    && payment.getPaymentDate().getYear() == today.getYear()
                    && payment.getPaymentDate().getMonth() == today.getMonth()) {
                monthlyRevenue += payment.getAmount();
            }
        }

        return new DashboardStats(activePolicies, pendingClaims, monthlyRevenue);
    }

    private List<RecentClaimView> recentClaimsForCurrentRole(int limit) throws RepositoryException {
        if (isAdmin()) {
            return dashboardRepository.getRecentClaims(limit);
        }

        List<RecentClaimView> recent = new ArrayList<>();
        for (Claim claim : visibleClaims()) {
            Customer customer = customerRepository.findCustomerById(claim.getCustomerId());
            Policy policy = policyRepository.findPolicyById(claim.getPolicyId());
            recent.add(new RecentClaimView(
                    claim.getClaimId(),
                    customer.getFullName(),
                    policyTypeName(policy),
                    claim.getStatus(),
                    claim.getClaimDate()
            ));
            if (recent.size() == limit) {
                break;
            }
        }
        return recent;
    }

    private List<PolicyDistribution> policyDistributionForCurrentRole() throws RepositoryException {
        if (isAdmin()) {
            return dashboardRepository.getPolicyDistribution();
        }

        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("LIFE", 0L);
        counts.put("HEALTH", 0L);
        counts.put("VEHICLE", 0L);

        for (Policy policy : visiblePolicies()) {
            String type = policyTypeName(policy);
            counts.put(type, counts.getOrDefault(type, 0L) + 1);
        }

        List<PolicyDistribution> distribution = new ArrayList<>();
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            if (entry.getValue() > 0) {
                distribution.add(new PolicyDistribution(entry.getKey(), entry.getValue()));
            }
        }
        return distribution;
    }

    private void updateSelectedUserStatus(TableView<User> table, String status) {
        User user = table.getSelectionModel().getSelectedItem();
        if (user == null) {
            showError("Please select a user first.");
            return;
        }

        save(() -> {
            userRepository.updateUserStatus(user.getUserId(), status);
            return user.getUserId();
        }, () -> loadUsers(table));
    }

    private void deleteSelectedUser(TableView<User> table) {
        User user = table.getSelectionModel().getSelectedItem();
        if (user == null) {
            showError("Please select a user first.");
            return;
        }
        if (user.getUserId() == loggedInUser.getUserId()) {
            showError("You cannot delete the account currently logged in.");
            return;
        }

        save(() -> {
            userRepository.deleteUser(user.getUserId());
            return user.getUserId();
        }, () -> loadUsers(table));
    }

    private void validateUserLoginRequest(UserLoginRequest request) throws RepositoryException {
        if (request.username().isBlank() || request.password().isBlank() || request.fullName().isBlank()) {
            throw new RepositoryException("Username, password, and full name are required.", null);
        }

        if ("ADMIN".equals(request.role())) {
            return;
        }

        if (request.referenceId() == null || request.referenceId() <= 0) {
            throw new RepositoryException("Agent and customer users need a valid reference ID.", null);
        }

        if ("AGENT".equals(request.role())) {
            agentRepository.findAgentById(request.referenceId());
        } else if ("CUSTOMER".equals(request.role())) {
            customerRepository.findCustomerById(request.referenceId());
        }
    }

    private void validateCustomerBeforeSave(Customer customer) throws RepositoryException {
        if (customer.getCustomerCode().isBlank()
                || customer.getFullName().isBlank()
                || customer.getEmail().isBlank()
                || customer.getPhone().isBlank()) {
            throw new RepositoryException("Customer code, name, email, and phone are required.", null);
        }

        Agent agent = agentRepository.findAgentById(customer.getAgentId());
        if (!"ACTIVE".equals(agent.getStatus())) {
            throw new RepositoryException("Customer can be assigned only to an active agent.", null);
        }

        if (isAgent() && customer.getAgentId() != currentReferenceId()) {
            throw new RepositoryException("Agents can create customers only under their own agent ID.", null);
        }

        for (Customer existing : customerRepository.findAllCustomers()) {
            if (existing.getCustomerCode().equalsIgnoreCase(customer.getCustomerCode())) {
                throw new RepositoryException("Customer code already exists. Use a unique customer code.", null);
            }
            if (existing.getEmail().equalsIgnoreCase(customer.getEmail())) {
                throw new RepositoryException("Customer email already exists. This prevents duplicate customer records.", null);
            }
            if (existing.getPhone().equalsIgnoreCase(customer.getPhone())) {
                throw new RepositoryException("Customer phone already exists. This prevents duplicate customer records.", null);
            }
        }
    }

    private void validatePolicyBeforeSave(Policy policy) throws RepositoryException {
        Customer customer = customerRepository.findCustomerById(policy.getCustomerId());
        Agent agent = agentRepository.findAgentById(policy.getAgentId());

        if (policy.getPolicyNumber().isBlank() || policy.getPolicyName().isBlank()) {
            throw new RepositoryException("Policy number and policy name are required.", null);
        }

        if (!"ACTIVE".equals(customer.getStatus()) || !"ACTIVE".equals(agent.getStatus())) {
            throw new RepositoryException("Policy can be issued only for active customers and agents.", null);
        }

        if (isAgent() && policy.getAgentId() != currentReferenceId()) {
            throw new RepositoryException("Agents can issue policies only under their own agent ID.", null);
        }

        if (isAgent() && customer.getAgentId() != currentReferenceId()) {
            throw new RepositoryException("This customer is not assigned to your agent login.", null);
        }

        for (Policy existing : policyRepository.findAllPolicies()) {
            if (existing.getPolicyNumber().equalsIgnoreCase(policy.getPolicyNumber())) {
                throw new RepositoryException("Policy number already exists. Use a unique policy number.", null);
            }
        }
    }

    private void validateClaimBeforeSave(Claim claim) throws RepositoryException {
        Policy policy = policyRepository.findPolicyById(claim.getPolicyId());
        if (claim.getClaimNumber().isBlank() || claim.getClaimReason().isBlank()) {
            throw new RepositoryException("Claim number and reason are required.", null);
        }

        if (policy.getCustomerId() != claim.getCustomerId()) {
            throw new RepositoryException("Claim customer must match the selected policy customer.", null);
        }

        if (!canAccessPolicy(policy)) {
            throw new RepositoryException("This policy is not assigned to your login.", null);
        }

        for (Claim existing : claimRepository.findAllClaims()) {
            if (existing.getClaimNumber().equalsIgnoreCase(claim.getClaimNumber())) {
                throw new RepositoryException("Claim number already exists. Use a unique claim number.", null);
            }
        }
    }

    private void validatePaymentBeforeSave(Payment payment) throws RepositoryException {
        Policy policy = policyRepository.findPolicyById(payment.getPolicyId());
        if (payment.getPaymentReference().isBlank()) {
            throw new RepositoryException("Payment reference is required.", null);
        }

        if (policy.getCustomerId() != payment.getCustomerId()) {
            throw new RepositoryException("Payment customer must match the selected policy customer.", null);
        }

        if (!canAccessPolicy(policy)) {
            throw new RepositoryException("This policy is not assigned to your login.", null);
        }

        for (Payment existing : paymentRepository.findAllPayments()) {
            if (existing.getPaymentReference().equalsIgnoreCase(payment.getPaymentReference())) {
                throw new RepositoryException("Payment reference already exists. Use a unique payment reference.", null);
            }
        }
    }

    private boolean canAccessPolicy(Policy policy) {
        if (policy == null) {
            return false;
        }

        if (isAdmin()) {
            return true;
        }

        if (isCustomer()) {
            return policy.getCustomerId() == currentReferenceId();
        }

        return isAgent() && policy.getAgentId() == currentReferenceId();
    }

    private boolean canWorkOnPolicy(Policy policy) {
        if (policy == null) {
            showError("Please select a policy first.");
            return false;
        }

        if (!canAccessPolicy(policy)) {
            showError("This policy is not assigned to your login.");
            return false;
        }

        return true;
    }

    private boolean canAccessCustomer(Customer customer) {
        if (customer == null) {
            return false;
        }
        return isAdmin()
                || (isAgent() && customer.getAgentId() == currentReferenceId())
                || (isCustomer() && customer.getCustomerId() == currentReferenceId());
    }

    private double totalPaid(List<Payment> payments) {
        double total = 0;
        for (Payment payment : payments) {
            if ("PAID".equals(payment.getStatus())) {
                total += payment.getAmount();
            }
        }
        return total;
    }

    private long countClaimsByStatus(List<Claim> claims, String status) {
        long total = 0;
        for (Claim claim : claims) {
            if (status.equals(claim.getStatus())) {
                total++;
            }
        }
        return total;
    }

    private String policyTypeName(Policy policy) {
        if (policy instanceof LifePolicy) {
            return "LIFE";
        }
        if (policy instanceof HealthPolicy) {
            return "HEALTH";
        }
        return "VEHICLE";
    }

    private boolean canAccessClaim(Claim claim) {
        if (claim == null) {
            return false;
        }

        if (isAdmin()) {
            return true;
        }

        if (isCustomer()) {
            return claim.getCustomerId() == currentReferenceId();
        }

        return isAgent() && isPolicyAssignedToAgent(claim.getPolicyId());
    }

    private boolean canWorkOnClaim(Claim claim) {
        if (claim == null) {
            showError("Please select a claim first.");
            return false;
        }

        if (!canAccessClaim(claim)) {
            showError("This claim is not assigned to your login.");
            return false;
        }

        return true;
    }

    private boolean isPolicyAssignedToAgent(long policyId) {
        try {
            // Agent can work only on policies assigned to their agent ID.
            Policy policy = policyRepository.findPolicyById(policyId);
            return policy.getAgentId() == currentReferenceId();
        } catch (RepositoryException exception) {
            return false;
        }
    }

    private boolean canViewAgents() {
        return isAdmin();
    }

    private boolean canViewCustomers() {
        return isAdmin() || isAgent();
    }

    private boolean canCreatePolicy() {
        return isAdmin() || isAgent();
    }

    private boolean canSubmitClaim() {
        return isAdmin() || isAgent() || isCustomer();
    }

    private boolean isAdmin() {
        return hasRole("ADMIN");
    }

    private boolean isAgent() {
        return hasRole("AGENT");
    }

    private boolean isCustomer() {
        return hasRole("CUSTOMER");
    }

    private boolean hasRole(String role) {
        return loggedInUser != null && role.equalsIgnoreCase(loggedInUser.getRole());
    }

    private long currentReferenceId() {
        return loggedInUser != null && loggedInUser.getReferenceId() != null
                ? loggedInUser.getReferenceId()
                : 0;
    }

    private Dialog<?> baseDialog(String title) {
        Dialog<?> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/insuretech.css").toExternalForm());
        return dialog;
    }

    @SuppressWarnings("unchecked")
    private <T> Dialog<T> dialog(String title) {
        return (Dialog<T>) baseDialog(title);
    }

    private GridPane formGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        return grid;
    }

    private void addRows(GridPane grid, String[] labels, javafx.scene.Node... nodes) {
        for (int index = 0; index < labels.length; index++) {
            Label label = new Label(labels[index]);
            label.getStyleClass().add("muted-label");
            grid.add(label, 0, index);
            grid.add(nodes[index], 1, index);
        }
    }

    private TextField field(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        return field;
    }

    private ComboBox<String> combo(String... values) {
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(values));
        comboBox.getSelectionModel().selectFirst();
        return comboBox;
    }

    private Button actionButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-button");
        button.setOnAction(event -> action.run());
        return button;
    }

    private Button dangerButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("danger-button");
        button.setOnAction(event -> action.run());
        return button;
    }

    private Label pageTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("page-title");
        return label;
    }

    private TableView<RecentClaimView> createRecentClaimsTable() {
        TableView<RecentClaimView> table = new TableView<>();
        addLongColumn(table, "Claim ID", RecentClaimView::getClaimId);
        addTextColumn(table, "Customer", RecentClaimView::getCustomerName);
        addTextColumn(table, "Policy", RecentClaimView::getPolicyType);
        addTextColumn(table, "Status", RecentClaimView::getStatus);
        TableColumn<RecentClaimView, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getClaimDate()));
        table.getColumns().add(dateColumn);
        return table;
    }

    private <T> void addTextColumn(TableView<T> table, String title, TextGetter<T> getter) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(getter.get(data.getValue())));
        table.getColumns().add(column);
    }

    private <T> void addLongColumn(TableView<T> table, String title, LongGetter<T> getter) {
        TableColumn<T, Number> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleLongProperty(getter.get(data.getValue())));
        table.getColumns().add(column);
    }

    private <T> void addDoubleColumn(TableView<T> table, String title, DoubleGetter<T> getter) {
        TableColumn<T, Number> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleDoubleProperty(getter.get(data.getValue())));
        table.getColumns().add(column);
    }

    private void save(SaveAction action, Runnable afterSave) {
        try {
            action.run();
            afterSave.run();
            showInfo("Saved successfully.");
        } catch (RepositoryException | RuntimeException exception) {
            showError(exception.getMessage());
        }
    }

    private void load(LoadAction action) {
        try {
            action.run();
        } catch (RepositoryException exception) {
            showError(exception.getMessage());
        }
    }

    private long parseLong(String value) {
        return Long.parseLong(value.trim());
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    private double parseDouble(String value) {
        return Double.parseDouble(value.trim());
    }

    private void applyStyle(Scene scene) {
        scene.getStylesheets().add(getClass().getResource("/styles/insuretech.css").toExternalForm());
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText("InsureTech Pro");
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText("Something went wrong");
        alert.showAndWait();
    }

    private record UserLoginRequest(String username, String password, String fullName, Long referenceId, String role) {
    }

    private interface TextGetter<T> {
        String get(T item);
    }

    private interface LongGetter<T> {
        long get(T item);
    }

    private interface DoubleGetter<T> {
        double get(T item);
    }

    private interface SaveAction {
        long run() throws RepositoryException;
    }

    private interface LoadAction {
        void run() throws RepositoryException;
    }
}
