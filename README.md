Project Overview
CompuStocker is an Android inventory management application designed for computer repair shops and PC builders. The app allows users to securely log in, track computer parts inventory, monitor stock levels, and receive low-stock alerts via SMS.

User Needs Addressed
Small repair shops needed a simple, fast way to track parts (CPUs, RAM, GPUs, etc.)
Real-time visibility of current stock and minimum stock thresholds
Ability to quickly add, update, and remove items
Notifications when stock is running low
Secure access with login/register functionality

App Features & UI Design
Key Screens:
Login/Register Screen – Username/password with login and register buttons
Inventory Screen – Grid layout displaying all items with name, part number, current quantity, min quantity, and delete option
Add Item Dialog – Clean form to add new parts with category selection (Spinner)
SMS Permission Dialog – Appears once on first launch to request SMS permissions for low-stock alerts

The UI was designed with the end user in mind:
Clean, modern Material Design with consistent spacing and colors
Grid layout for easy visual scanning of inventory
Floating Action Button for quick access to “Add Item”
Truncated text and consistent card heights for professional appearance
Simple navigation and clear labels

Development Approach
I followed the MVC (Model-View-Controller) pattern as required:
Models – User, Category, InventoryItem
Views – XML layouts and dialogs
Controllers – Activities placed in the controller package

Techniques Used:
SQLite database with full CRUD operations
RecyclerView + GridLayoutManager for inventory display
Dialogs for adding items and SMS permissions
SharedPreferences to show SMS dialog only once
Proper permission handling for SMS

These strategies (separation of concerns, reusable adapters, and database-first design) are techniques I will definitely carry forward into future projects.
Testing ProcessI tested the app on:
Android Emulator (API 30, 34, and 35)
Physical Android device

Testing Included:
Login / Registration functionality
Adding, editing quantities, and deleting items
Database persistence (data survives app restart)
SMS permission flow and test message sending
UI responsiveness on different screen sizes

This testing process revealed several small UI alignment issues and helped me improve error handling in the add item form.

Challenges & Innovation
One challenge was making the inventory grid cards have consistent heights while displaying variable-length part numbers. I solved this by using a combination of minHeight, truncation (ellipsize), and Space views with layout_weight to keep quantity controls aligned at the bottom of every card.
Another challenge was properly implementing the “show SMS permission only once” requirement — solved using SharedPreferences.
Area of Success
I am particularly proud of the inventory management system (RecyclerView + Database integration + Add Item Dialog). The combination of a clean grid UI, real-time quantity editing, low-stock awareness, and seamless database operations demonstrates strong Android development skills using modern components and proper architecture.

