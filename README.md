# PennyWise Documentation

## GROUP MEMEBERS (Contributors)
### Nkosinathi Ngozo - ST10215069
### Thobani Khumalo - ST10092282
### Samendra Morgan - ST10092045


## Main project repository
- App Code repo - https://github.com/system-sculpters/OPSC7312
- Rest Api Repo - https://github.com/system-sculpters/OPSC-API.git

# Recommended System requirements
- windows 8-11
- minimum of 8 GB of free hard disk space
- Fast processor
- 2GB of ram

# Compiling software
- Android Studio

# Running software
- Copy the repository link from github
- Open Android studio
- Click the 'Get from VCS' button
- Paste the link URL input field
- Click the 'Clone' button
- Click on the 'Trust Project' button
- Allow the Gradle files to be configured
- Once the Gradle files are configured click on the green 'play' Button to run the project

## Technology Stack
 - Frontend: Kotlin (for Android app development)
- Backend: Node.js with Express
- Database: Firebase Firestore
- Version Control: Git
- CI/CD: GitHub Actions

## 1. Introduction
The PennyWise app is a comprehensive personal finance management tool designed to help users efficiently manage their budgets, track transactions, set financial goals, and monitor analytics related to income and expenses. 


## 2. Purpose of the App

### The purpose of PennyWise is to:

- Help users manage their personal finances by providing income and expense tracking, and goal-setting tools.
- Offer clear analytics for both short-term (weekly) and long-term (annual) financial insights.
- Provide seamless interaction with a RESTful API connected to a cloud-based database, ensuring secure data storage and retrieval.

The primary goal of PennyWise is to give users control over their financial planning with user-friendly interfaces and intuitive features that make tracking income, expenses, and savings goals straightforward.


## 3. Design Considerations
The app's design prioritizes both usability and security. Key design considerations include:

### API Design Principles
![alt text](image-5.png)

- RESTful Design: Follow RESTful principles, such as using appropriate HTTP methods (GET, POST, PUT, DELETE), and ensuring stateless interactions.

- Consistent Error Responses: Design a consistent error response format to make it easy for developers to handle errors. Include meaningful error codes and messages.

- Ease of Use: Ensure the API is intuitive and easy to use, with logical endpoint naming and clear request/response structures.

- Efficient Querying: Optimize queries to the Firebase database to ensure fast data retrieval. Use Firebase's indexing and querying capabilities to improve performance for large datasets.

- Authentication and Authorization: Implement Firebase Authentication to manage user identities securely. Ensure that API endpoints are protected so that users can only access their own data. Use Firebase's security rules to enforce data access controls based on user roles and permissions.

![alt text](image-6.png)


### UI/UX Design:
![alt text](IMG-20240930-WA0025.jpg)

- A simple and intuitive layout that ensures users can easily navigate between different features like transactions, goals, categories, and analytics.
Use of Material Design components to provide a familiar and responsive interface for Android users.

#### For Easy Navigation:
- Bottom Navigation Menu: This serves as the primary navigation tool, allowing users to switch between the app’s key features such as Dashboard, Transactions, Transaction creation, Analytics and Settings. Each section is easily accessible with intuitive icons and labels at the bottom of the screen, ensuring seamless navigation.

![alt text](image-3.png)

- Side Navigation Menu (Top Right): Positioned at the top-right corner, this secondary navigation menu provides quick access to additional features, including Categories, Goals and Investment profile (to be implemented in final POE). It slides in from the right and is available on all screens, making it convenient for users to less frequent tasks without leaving the current screen.

![alt text](IMG-20240930-WA0020.jpg)

- Back Button (Top Left): This button appears at the top-left corner on each screen to allow users to return to the previous screen easily. It ensures smooth backward navigation throughout the app, helping users maintain context and avoid confusion.

![alt text](image-4.png)

### Security and User Management:

### User Registration:

- User data: Users can register for the PennyWise app by providing their username, email and password. This process is handled by Firebase Authentication, which creates a new user account in its system.

![alt text](<WhatsApp Image 2024-09-30 at 17.24.31_f51ce2c7.jpg>)


#### User Login:

- Process: To log in, users enter their email and password. Firebase Authentication verifies these credentials and grants access if they are correct. This ensures that only authorized users can access their data.

![alt text](<WhatsApp Image 2024-09-30 at 17.24.31_087da437.jpg>)

#### Single Sign-On (SSO): 
- SSO allows users to register/login using their existing credentials from another service (specifically Google), simplifying the registration and login process.

![alt text](IMG-20240930-WA0028.jpg)

#### Profile management
- Updating Profile Information: Once logged in, users can update their profile information, such as their display name. Firebase Authentication allows for updating user profiles, which reflects these changes across the app.

![alt text](IMG-20240929-WA0005-1.jpg)

### Settings
The Settings screen provides users with control over various aspects of the app’s functionality and appearance, allowing for a personalized experience:

![alt text](IMG-20240930-WA0021.jpg)

- Profile Settings: Users can view and update their personal data, such as their name, email, and password. This ensures that they have up-to-date information and secure access to the app.

![alt text](IMG-20240929-WA0005.jpg)

- Language Settings: This option allows users to change the app’s language to their preferred one, utilizing multi-language support to cater to different user needs (fully working feature to be implemented in final POE).

![alt text](IMG-20240930-WA0016.jpg)

- Security Settings: Users can toggle fingerprint authentication on or off for additional security. This ensures that they can choose between a standard login method and biometric authentication based on their preference (fully working feature to be implemented in final POE).

![alt text](IMG-20240930-WA0018.jpg)


- Theme Settings: The app provides options to switch between light, dark, or system-based themes, allowing users to adjust the appearance based on personal preference or system-wide settings.

![alt text](IMG-20240930-WA0017.jpg)

- Notifications Settings: Users can modify what type of notification alerts they receive, including updates about transactions, goals, and system messages (fully working feature to be implemented in final POE).

![alt text](IMG-20240930-WA0014.jpg)

All these settings are stored using shared preferences, ensuring that user configurations are saved locally on their device for consistent and seamless interaction with the app.

### Transaction Management:

Transaction management in the PennyWise app allows users to record and track both income and expense transactions. Each transaction is categorized to provide a clear overview of financial activity, making budgeting and analysis more effective. Users can create, view, update, and delete both income and expense transactions. 

![alt text](IMG-20240930-WA0027.jpg)

- Income Transactions: Represent any money added to the user’s account (e.g., salary, freelance payments). These transactions increase the user's overall balance and are displayed in financial analytics to show trends in income over time.

- Expense Transactions: Track any spending by the user (e.g., bills, groceries). These transactions reduce the overall balance and are categorized for easy analysis of where the user's money is being spent.

### Category Management:

Category management in the PennyWise app is a core feature that enables users to organize their financial transactions into meaningful groups, making it easier to analyze spending and income patterns. Categories serve as the backbone for tracking both income and expense transactions, offering a structured approach to personal finance management.

![alt text](IMG-20240930-WA0015.jpg) 


![alt text](IMG-20240930-WA0013.jpg)

### Key Features of Category Management:
- Create Custom Categories: Users can create new categories tailored to their specific financial activities. This allows for greater flexibility, ensuring that transactions are accurately categorized according to the user's unique needs. For example, users might create categories such as "Dining", "Freelance Income," or "Subscriptions."

- Edit and Delete Categories: Users have the ability to modify existing categories to better suit their changing financial habits. For example, users can rename a category or delete one that’s no longer relevant. This ensures that the system remains adaptable to the user's financial situation.

- Assigning Categories to Transactions: When creating a transaction, users must assign it to a specific category. This categorization helps organize the user’s transactions and provides detailed insights into how much money is flowing in and out of specific areas. Categories play a critical role in financial analysis, helping users track spending patterns and see areas where they can save or allocate resources more efficiently.

- Category-Based Analytics: Category management directly influences the analytics features of the app. By categorizing transactions, users can generate visual reports and summaries based on category-specific data. This includes seeing how much has been spent or earned in a given category over a specific time frame, helping users make informed decisions and improve their budgeting strategies.

### Goal Tracking:

The Goal Tracking feature in the PennyWise app is designed to help users set and achieve their financial objectives, providing a clear path to saving for both short-term and long-term goals. This feature is fully integrated into the app’s budgeting and transaction management systems, ensuring that users can track their progress seamlessly as they manage their finances.

### Key Features of Goal Tracking:
- Create Financial Goals: Users can set up specific financial goals, such as saving for a vacation, a new car, or an emergency fund. Each goal is defined by a target amount, a description, and a deadline, giving users a clear objective to work towards. For example, a user may create a goal to save R5,000 for a down payment on a car by the end of the year.

![alt text](IMG-20240930-WA0010.jpg)


- Track Progress in Real-Time: The app provides real-time updates on the user’s progress toward their goals. As users save money or allocate funds to specific goals, they can see how much they’ve contributed and how far they are from reaching their target. This is presented through visual indicators, such as progress bars or percentage trackers, making it easy to see how close they are to achieving their goals.

![alt text](IMG-20240930-WA0012.jpg)

- Goal Analytics and Reports: The app includes a goal-specific analytics feature, allowing users to review their saving patterns over time and see how much they’ve contributed to each goal. This historical data provides valuable insights into their financial habits and allows them to adjust their saving strategies as needed.


### Analytics and Reporting:

This feature provides users with comprehensive insights into their financial behavior, allowing them to make informed decisions and effectively manage their budget, transactions, and goals. By breaking down income and expenses into meaningful data visualizations and detailed reports, users can track their financial health over time and identify areas for improvement.

![alt text](<WhatsApp Image 2024-09-30 at 17.18.15_ddc43a7b.jpg>)

![alt text](IMG-20240930-WA0024.jpg)


### Key Features of Analytics and Reporting:
- Income and Expense Breakdown: Users can view a detailed analysis of their income and expenses over the last week or 6-months. 

- Goal Progress Reports: Integrated with the goal tracking feature, users can access specific reports on the progress of their financial goals. They can review how much they’ve saved toward a goal each month and how much more is needed to meet their targets. This report shows contributions over time, helping users stay motivated and on track.

- Category-Specific Analytics: This is a pie chart that visually represents the split of expenses across different categories. This chart helps users quickly understand where most of their money is being spent by displaying the percentage distribution for each category

- Income: This is a dynamic graph that displays income trends over the past 12, 6, and 3 months. This visual tool helps users to track their earnings and identify patterns, such as seasonal changes or income growth. The graph allows users to switch between these timeframes for a quick, comparative view of their financial health. For example, users can easily assess how their income has fluctuated and identify whether it has increased, remained stable, or decreased over the selected periods


## 4. GitHub Utilisation
PennyWise’s development will be version-controlled and managed through GitHub, following best practices for code collaboration and maintenance. The GitHub repository will include:

- Version Control: Both the API and the main application have their own repositories to manage their source code independently. This separation allows for focused development and version tracking specific to each component. The API repository contains the backend code, including business logic, database interactions, and RESTful endpoints. The main application repository contains the front-end code, such as user interfaces and client-side interactions. This setup supports independent updates and deployments, facilitating better management of changes and minimizing potential conflicts between the API and application.
- API
![alt text](image-1.png)

- APPLICATION
![alt text](image-2.png)

- API Deployment Automation: Render is configured to automatically deploy your API whenever there are updates to the repository.

![alt text](image.png)

- Pull Requests (PRs): Pull Requests will be used to review code and ensure that the codebase remains stable. Each PR will be reviewed and tested before being merged into the main branch.



- Documentation: The repository will include detailed README files, API documentation, and setup instructions, making it easy for new developers to contribute to the project. Documentation will also cover database schema, API endpoints, and app architecture.


## 5. GitHub Actions

![alt text](image-7.png)

![alt text](image-8.png)

![alt text](image-9.png)


- GitHub Actions is a powerful feature integrated into GitHub that automates workflows within the development proces. By using GitHub Actions, developers we created workflows that respond to various events in our repository, such as pushes and pull requests. Here are some key functionalities of GitHub Actions utilized in this project:

- Continuous Integration
Continuous Integration (CI) is a practice that encourages developers to integrate their changes into the main codebase frequently. With GitHub Actions, CI is automated to ensure that the application is built and tested every time code is pushed to the repository or a pull request is created. This process involves several steps:

- Automatic Build: Whenever changes are pushed, GitHub Actions triggers a build process that compiles the application. This ensures that the new code integrates smoothly with the existing codebase without introducing compilation errors.

- Code Quality Checks: During the build process, automated checks are run to analyze the code quality. These checks help maintain coding standards across the project.

- Unit Testing Execution: Whenever code is pushed or a pull request is created, GitHub Actions automatically triggers the execution of unit tests. These tests validate the functionality of individual components of the application, ensuring that each part behaves as expected.

