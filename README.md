# Invoice Management System  
![Contributors](https://img.shields.io/github/contributors/mmaisonnave/invoice-management-system?style=plastic)
![Forks](https://img.shields.io/github/forks/mmaisonnave/invoice-management-system)
![Stars](https://img.shields.io/github/stars/mmaisonnave/invoice-management-system)
![GitHub](https://img.shields.io/github/license/mmaisonnave/invoice-management-system?style=round-square)
![Issues](https://img.shields.io/github/issues/mmaisonnave/invoice-management-system)

### Summary  
This repository contains the source code for a custom-built invoice-handling system designed to meet the unique needs of an accounting firm in Argentina. The software and documentation are primarily in Spanish, with partial English translations available.  

The system is built with Java and SQL for backend operations, paired with a JavaFX-based frontend. It efficiently manages database operations, tracks transaction and invoice histories, and generates polished PDF reports using JasperReports.  

### Features  

Here’s what the system can do:  
- **Invoice Creation:** Create detailed invoices that include job descriptions, charges for each task, taxes, and dates.  
- **Invoice Drafting and Bulk Operations:** Draft invoices are stored in the database and support bulk operations. For example, draft invoices can be auto-generated for all active clients for the upcoming month using data from the previous month (assuming similar tasks are performed monthly, which was a common scenario for the requesting firm). Bulk features include price adjustments (e.g., inflation increases) and allow users to review, modify, and finalize invoices collectively. Drafts can be previewed with watermarks and printed for clients.  
- **Customer Balance Management:** Tracks customer balances, showing whether they owe money (unpaid invoices), have credit (overpayments), or have settled their account (zero balance). Users can register payments, issue or cancel invoices, and view the full transaction history for any client. This history can be fetched and printed for client reference.  
- **Invoice History and Draft Management:** Access the complete history of issued invoices (read-only) and manage draft invoices (read and edit).  
- **Client Management:** Add or update client details and flag clients as active or inactive. Inactive clients remain in the database for historical purposes but are excluded from bulk invoice generation.  

### Technical Details  

##### System Architecture  
- **Report Generation:** Uses JasperReports to create detailed PDF reports, such as invoices and transaction summaries.  
- **Database Management:** Relies on MariaDB for storage and SQL for database queries and updates.  
- **Frontend:** Built with JavaFX, offering an intuitive and user-friendly interface.  

##### Code Organization  
- **Database Code:** Located in the `db` folder, with core functionality in `DBEngine.java`.  
- **Report Code:** Found in the `reports` folder, with primary logic in `ReportsEngine.java`.  
- **Frontend Code:** Stored in the `view` folder, utilizing FXML files to define the interface.  
- **Report Templates:** Saved in the `report_templates` folder and used for generating client-specific invoices and balance summaries.  


## Authors
* [Mariano Maisonnave](https://github.com/mmaisonnave)
* [Vir Sabando](https://github.com/VirSabando)


### License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details


### Contributing
We welcome contributions from the community. Please open an issue or submit a pull request for any improvements or suggestions.