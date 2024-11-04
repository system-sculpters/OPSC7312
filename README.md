# PennyWise Documentation

## GROUP MEMEBERS (Contributors)
### Nkosinathi Ngozo - ST10215069
### Thobani Khumalo - ST10092282
### Samendra Morgan - ST10092045


## Main project repositories
- App Code repo - https://github.com/system-sculpters/OPSC7312
- Rest Api Repo - https://github.com/system-sculpters/OPSC7312-POE-API.git

# DEMONSTRATION VIDEOS
- https://youtu.be/pfvdVw22kkM
- https://youtu.be/_0A5oazaZ6Y

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

![image](https://github.com/user-attachments/assets/ee0edd82-fdd7-418e-bb7b-6a24ea2bfdb1)

- RESTful Design: Follow RESTful principles, such as using appropriate HTTP methods (GET, POST, PUT, DELETE), and ensuring stateless interactions.

- Consistent Error Responses: Design a consistent error response format to make it easy for developers to handle errors. Include meaningful error codes and messages.

- Ease of Use: Ensure the API is intuitive and easy to use, with logical endpoint naming and clear request/response structures.

- Efficient Querying: Optimize queries to the Firebase database to ensure fast data retrieval. Use Firebase's indexing and querying capabilities to improve performance for large datasets.

- Authentication and Authorization: Implement Firebase Authentication to manage user identities securely. Ensure that API endpoints are protected so that users can only access their own data. Use Firebase's security rules to enforce data access controls based on user roles and permissions.


![image](https://github.com/user-attachments/assets/68ab8c09-46fe-4886-9da7-f68be3359013)



### UI/UX Design:
![IMG-20240930-WA0025](https://github.com/user-attachments/assets/dcc0b5dc-ef95-4d20-9c40-bbffeb9b49ce)

- A simple and intuitive layout that ensures users can easily navigate between different features like transactions, goals, categories, and analytics.
Use of Material Design components to provide a familiar and responsive interface for Android users.

#### For Easy Navigation:
- Bottom Navigation Menu: This serves as the primary navigation tool, allowing users to switch between the app’s key features such as Dashboard, Transactions, Transaction creation, Analytics and Settings. Each section is easily accessible with intuitive icons and labels at the bottom of the screen, ensuring seamless navigation.

![image](https://github.com/user-attachments/assets/25ff7b7c-913e-4d1d-b258-a0d2cb784dd9)


- Side Navigation Menu (Top Right): Positioned at the top-right corner, this secondary navigation menu provides quick access to additional features, including Categories, Goals and Investment profile (to be implemented in final POE). It slides in from the right and is available on all screens, making it convenient for users to less frequent tasks without leaving the current screen.

![IMG-20240930-WA0020](https://github.com/user-attachments/assets/5c0f70a1-a00b-47cb-b504-7c64280281b1)


- Back Button (Top Left): This button appears at the top-left corner on each screen to allow users to return to the previous screen easily. It ensures smooth backward navigation throughout the app, helping users maintain context and avoid confusion.

![image](https://github.com/user-attachments/assets/776e2ee1-793e-46a0-bca0-4c51d3bad49d)


### Security and User Management:

### User Registration:

- User data: Users can register for the PennyWise app by providing their username, email and password. This process is handled by Firebase Authentication, which creates a new user account in its system.

![WhatsApp Image 2024-09-30 at 17 24 31_f51ce2c7](https://github.com/user-attachments/assets/87dce343-4879-4c42-98cc-49e6b4832d5b)


#### User Login:

- Process: To log in, users enter their email and password. Firebase Authentication verifies these credentials and grants access if they are correct. This ensures that only authorized users can access their data.


![WhatsApp Image 2024-09-30 at 17 24 31_087da437](https://github.com/user-attachments/assets/785316a2-2674-4801-bdd4-f30a9e5f5e4f)



#### Single Sign-On (SSO): 
- SSO allows users to register/login using their existing credentials from another service (specifically Google), simplifying the registration and login process.

![IMG-20240930-WA0028](https://github.com/user-attachments/assets/12aa6ad3-1aea-4e9e-b1eb-adb5ae0d3bbb)


#### Profile management
- Updating Profile Information: Once logged in, users can update their profile information, such as their display name. Firebase Authentication allows for updating user profiles, which reflects these changes across the app.


![IMG-20240930-WA0019](https://github.com/user-attachments/assets/391f127b-6b6c-44aa-8cd2-5296dca4112e)

### Settings
The Settings screen provides users with control over various aspects of the app’s functionality and appearance, allowing for a personalized experience:

![IMG-20240930-WA0021](https://github.com/user-attachments/assets/da2788f0-fe38-4caa-b6e2-a74a55510702)


- Profile Settings: Users can view and update their personal data, such as their name, email, and password. This ensures that they have up-to-date information and secure access to the app.

![IMG-20240930-WA0019](https://github.com/user-attachments/assets/391f127b-6b6c-44aa-8cd2-5296dca4112e)

- Language Settings: This option allows users to change the app’s language to their preferred one, utilizing multi-language support to cater to different user needs (fully working feature to be implemented in final POE).

![IMG-20240930-WA0016](https://github.com/user-attachments/assets/f97bb051-7103-41f9-b578-56ecc64501d6)


- Security Settings: Users can toggle fingerprint authentication on or off for additional security. This ensures that they can choose between a standard login method and biometric authentication based on their preference (fully working feature to be implemented in final POE).


![IMG-20240930-WA0018](https://github.com/user-attachments/assets/3a905482-ac26-4f88-9a09-3fa30048a4bf)

- Theme Settings: The app provides options to switch between light, dark, or system-based themes, allowing users to adjust the appearance based on personal preference or system-wide settings.

![IMG-20240930-WA0017](https://github.com/user-attachments/assets/05e0b730-6359-41e5-b885-08b68cbd847b)


- Notifications Settings: Users can modify what type of notification alerts they receive, including updates about transactions, goals, and system messages (fully working feature to be implemented in final POE).

![IMG-20240930-WA0014](https://github.com/user-attachments/assets/fee99cac-e397-4a10-ae3e-52749a13fc4d)


All these settings are stored using shared preferences, ensuring that user configurations are saved locally on their device for consistent and seamless interaction with the app.

### Transaction Management:

Transaction management in the PennyWise app allows users to record and track both income and expense transactions. Each transaction is categorized to provide a clear overview of financial activity, making budgeting and analysis more effective. Users can create, view, update, and delete both income and expense transactions. 

![IMG-20240930-WA0027](https://github.com/user-attachments/assets/7038937a-4eff-46c2-a1d5-8d0583832b2a)

![IMG-20240930-WA0026](https://github.com/user-attachments/assets/af434e13-2f95-4879-af86-d8a92f7ce122)

![IMG-20240930-WA0022](https://github.com/user-attachments/assets/3890f5a5-2292-4476-ae97-0451dc677e4b)

- Income Transactions: Represent any money added to the user’s account (e.g., salary, freelance payments). These transactions increase the user's overall balance and are displayed in financial analytics to show trends in income over time.

- Expense Transactions: Track any spending by the user (e.g., bills, groceries). These transactions reduce the overall balance and are categorized for easy analysis of where the user's money is being spent.

### Category Management:

Category management in the PennyWise app is a core feature that enables users to organize their financial transactions into meaningful groups, making it easier to analyze spending and income patterns. Categories serve as the backbone for tracking both income and expense transactions, offering a structured approach to personal finance management.


![IMG-20240930-WA0015](https://github.com/user-attachments/assets/8318a7f4-28c7-4ff4-835b-ef8c172b6df9)


![IMG-20240930-WA0013](https://github.com/user-attachments/assets/324e6837-91b8-49f7-93e5-e3e47403e14b)



### Key Features of Category Management:
- Create Custom Categories: Users can create new categories tailored to their specific financial activities. This allows for greater flexibility, ensuring that transactions are accurately categorized according to the user's unique needs. For example, users might create categories such as "Dining", "Freelance Income," or "Subscriptions."

- Edit and Delete Categories: Users have the ability to modify existing categories to better suit their changing financial habits. For example, users can rename a category or delete one that’s no longer relevant. This ensures that the system remains adaptable to the user's financial situation.

- Assigning Categories to Transactions: When creating a transaction, users must assign it to a specific category. This categorization helps organize the user’s transactions and provides detailed insights into how much money is flowing in and out of specific areas. Categories play a critical role in financial analysis, helping users track spending patterns and see areas where they can save or allocate resources more efficiently.

- Category-Based Analytics: Category management directly influences the analytics features of the app. By categorizing transactions, users can generate visual reports and summaries based on category-specific data. This includes seeing how much has been spent or earned in a given category over a specific time frame, helping users make informed decisions and improve their budgeting strategies.

### Goal Tracking:

The Goal Tracking feature in the PennyWise app is designed to help users set and achieve their financial objectives, providing a clear path to saving for both short-term and long-term goals. This feature is fully integrated into the app’s budgeting and transaction management systems, ensuring that users can track their progress seamlessly as they manage their finances.

### Key Features of Goal Tracking:
- Create Financial Goals: Users can set up specific financial goals, such as saving for a vacation, a new car, or an emergency fund. Each goal is defined by a target amount, a description, and a deadline, giving users a clear objective to work towards. For example, a user may create a goal to save R5,000 for a down payment on a car by the end of the year.

![IMG-20240930-WA0010](https://github.com/user-attachments/assets/85dbffa6-f205-49c5-8a75-888617a11ac6)


- Track Progress in Real-Time: The app provides real-time updates on the user’s progress toward their goals. As users save money or allocate funds to specific goals, they can see how much they’ve contributed and how far they are from reaching their target. This is presented through visual indicators, such as progress bars or percentage trackers, making it easy to see how close they are to achieving their goals.


![IMG-20240930-WA0012](https://github.com/user-attachments/assets/7cdd8a48-7433-4652-8a1a-fc65da2680f9)


- Goal Analytics and Reports: The app includes a goal-specific analytics feature, allowing users to review their saving patterns over time and see how much they’ve contributed to each goal. This historical data provides valuable insights into their financial habits and allows them to adjust their saving strategies as needed.


### Analytics and Reporting:

This feature provides users with comprehensive insights into their financial behavior, allowing them to make informed decisions and effectively manage their budget, transactions, and goals. By breaking down income and expenses into meaningful data visualizations and detailed reports, users can track their financial health over time and identify areas for improvement.


![IMG-20240930-WA0024](https://github.com/user-attachments/assets/1770bf9e-3e1d-4831-8883-e4d93fd9fa0f)

![IMG-20240930-WA0023](https://github.com/user-attachments/assets/b20102ec-b974-4bcd-a71f-1b13317465d6)


### Language Selection 

This feature enables users to switch seamlessly between **English**, **Afrikaans**, and **Zulu**. This feature allows users to navigate and interact with the app in their preferred language, enhancing accessibility and creating a more personalized user experience. To change the language, simply go to the settings menu, select "Language," and choose from the available options.

![WhatsApp Image 2024-11-02 at 16 13 39 (1)](https://github.com/user-attachments/assets/0093079d-7852-4b84-aa44-c5d94c1396c7)
![WhatsApp Image 2024-11-02 at 16 13 39](https://github.com/user-attachments/assets/29bd4bef-5539-497f-88e6-3423977e573e)

### Stock Investment (Demo):

This feature that allows users to virtually buy and sell stocks in a simulated environment. This feature enables users to experience real-time market dynamics without any financial risk, helping them build confidence and practice investment strategies. Users can track the performance of their virtual portfolio, experiment with buying and selling, and gain valuable insights into the stock market—all in a risk-free setting designed for learning and skill-building.


![WhatsApp Image 2024-11-02 at 16 01 14](https://github.com/user-attachments/assets/cbeb1739-2f80-495b-a35a-2b7179e199b0)

-Portfolio: This feature provides users with a comprehensive view of their current stock portfolio, displaying all purchased stocks along with the real-time value of each. Users can easily monitor their investments and track how each stock is performing, offering a clear snapshot of their overall portfolio health.

![WhatsApp Image 2024-11-02 at 16 01 14 (2)](https://github.com/user-attachments/assets/0bdcf4ed-32c1-4754-848a-517b278d5f9f)

-Markets: This feature enables users to explore and search through a wide selection of available stocks. Users can browse various investment options and choose stocks to buy or sell, making it easy to discover opportunities and manage their virtual investments.

![WhatsApp Image 2024-11-02 at 16 01 14 (1)](https://github.com/user-attachments/assets/277fd124-33bc-48f5-a3dd-3f941ab7405b)

-View-Stock: This feature allows users to view a real-time graph of a selected stock's performance, providing a quick snapshot of recent trends to help them decide whether to buy or sell.

![WhatsApp Image 2024-11-02 at 16 01 15 (1)](https://github.com/user-attachments/assets/c4c48e4a-aca6-45d2-af7d-d07afcc3ce5e)

-Buy Stock: This feature enables users to virtually purchase stocks, specifying the desired quantity and viewing the total purchase value. It provides an easy and interactive way to add stocks to their portfolio while understanding the cost of their investment.

![WhatsApp Image 2024-11-02 at 16 29 41](https://github.com/user-attachments/assets/2d280b71-8e21-4ad9-b16e-c519e47d18a5)

-Portfolio: After purchasing stocks, users can view them directly in their portfolio, allowing for easy access and management of their investments in one consolidated location.

### Key Features of Analytics and Reporting:
- Income and Expense Breakdown: Users can view a detailed analysis of their income and expenses over the last week or 6-months. 

- Goal Progress Reports: Integrated with the goal tracking feature, users can access specific reports on the progress of their financial goals. They can review how much they’ve saved toward a goal each month and how much more is needed to meet their targets. This report shows contributions over time, helping users stay motivated and on track.

- Category-Specific Analytics: This is a pie chart that visually represents the split of expenses across different categories. This chart helps users quickly understand where most of their money is being spent by displaying the percentage distribution for each category

- Income: This is a dynamic graph that displays income trends over the past 12, 6, and 3 months. This visual tool helps users to track their earnings and identify patterns, such as seasonal changes or income growth. The graph allows users to switch between these timeframes for a quick, comparative view of their financial health. For example, users can easily assess how their income has fluctuated and identify whether it has increased, remained stable, or decreased over the selected periods


## 4. GitHub Utilisation
PennyWise’s development will be version-controlled and managed through GitHub, following best practices for code collaboration and maintenance. The GitHub repository will include:

- Version Control: Both the API and the main application have their own repositories to manage their source code independently. This separation allows for focused development and version tracking specific to each component. The API repository contains the backend code, including business logic, database interactions, and RESTful endpoints. The main application repository contains the front-end code, such as user interfaces and client-side interactions. This setup supports independent updates and deployments, facilitating better management of changes and minimizing potential conflicts between the API and application.
- API
![image](https://github.com/user-attachments/assets/ed28c5c4-4983-44c6-841c-7f3dce7caabc)

- APPLICATION
![image](https://github.com/user-attachments/assets/825198bb-af1c-46c5-92b6-784f563e2a27)


- API Deployment Automation: Render is configured to automatically deploy your API whenever there are updates to the repository.

![image](https://github.com/user-attachments/assets/97fdc3e0-8949-4aff-8261-9545e8bbe0b0)


![image](https://github.com/user-attachments/assets/93381a5c-5395-4d04-824f-8c3ac4db105e)


- Pull Requests (PRs): Pull Requests will be used to review code and ensure that the codebase remains stable. Each PR will be reviewed and tested before being merged into the main branch.



- Documentation: The repository will include detailed README files, API documentation, and setup instructions, making it easy for new developers to contribute to the project. Documentation will also cover database schema, API endpoints, and app architecture.


## 5. GitHub Actions

![image](https://github.com/user-attachments/assets/7dc04508-d74a-45b9-8c0c-3dcecfd8b7ac)

![image](https://github.com/user-attachments/assets/b0731d01-93b0-4da6-bb17-d521c32a5fcf)

![Screenshot 2024-11-04 015714](https://github.com/user-attachments/assets/3f4ca603-6136-4f87-b65c-2f86ea19bc23)

![Screenshot 2024-11-04 015751](https://github.com/user-attachments/assets/ab3c2b00-aa8d-45c3-82e9-d98680efafe8)

- GitHub Actions is a powerful feature integrated into GitHub that automates workflows within the development proces. By using GitHub Actions, developers we created workflows that respond to various events in our repository, such as pushes and pull requests. Here are some key functionalities of GitHub Actions utilized in this project:

- Continuous Integration
Continuous Integration (CI) is a practice that encourages developers to integrate their changes into the main codebase frequently. With GitHub Actions, CI is automated to ensure that the application is built and tested every time code is pushed to the repository or a pull request is created. This process involves several steps:

- Automatic Build: Whenever changes are pushed, GitHub Actions triggers a build process that compiles the application. This ensures that the new code integrates smoothly with the existing codebase without introducing compilation errors.

- Code Quality Checks: During the build process, automated checks are run to analyze the code quality. These checks help maintain coding standards across the project.

- Unit Testing Execution: Whenever code is pushed or a pull request is created, GitHub Actions automatically triggers the execution of unit tests. These tests validate the functionality of individual components of the application, ensuring that each part behaves as expected.


## 6. PennyWise Release Notes (file found in repo)
Release Notes for v2.1.0 
Date: November 4, 2024 

#### Summary 
We’re thrilled to introduce new features in this release, including language support, real-time stock updates, biometric authentication and offline functionality. These additions make the app more accessible, powerful, and user
friendly.

#### New Features: 
- Language Options: Users can now choose between Zulu and Afrikaans, enhancing accessibility 
and user experience for a wider audience.
- Push Notifications: Stay informed with real-time notifications about your portfolio, stock prices, and more. 
- Biometric Authentication: Secure your account with fingerprint authenticaion for a faster, more secure login experience. 
- Offline Mode with Sync: Access your data even without an internet connection. Any changes 
made offline will automatically sync when you reconnect. 

#### Innovative Features 
- Real-Time Stock Exchange View: Get live stock prices and monitor market trends in real time for informed decision-making. 
- Portfolio Trading (Demo): Try out stock trading in a simulated environment, allowing you to buy and sell stocks and track your portfolio without real financial risk.

#### Bug fixes: 
- Fixed: When clicked on the transaction details the app would crash the bug was fixed and users can now view transaction details


## 7. Evidence of preparation for publication
All files found in the 'Evidence of preparation for publication' folder and contains (signed APK, screenshots and screenshots of the app uploaded to google play console)

privacy policy: https://system-sculpters.github.io/pennywise-privacy-policy/

Screenshot of the app uploaded on the Google Play Console

![image](https://github.com/user-attachments/assets/0c6a0509-4d2d-4efa-92b8-961b691b8913)


![image](https://github.com/user-attachments/assets/4702a17b-ac1c-4b97-8e0e-78965a05754c)

 
![image](https://github.com/user-attachments/assets/ed54f4bc-6124-4f9f-85a7-0e0384a2fcce)


