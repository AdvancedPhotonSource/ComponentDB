# Using CDB CLI Utilities 
## cdbSearch
This CLI allows a user to search for an item in CDB. 
### Usage
```sh
$ cdbSearch [searchString]
```
#### searchString
- When search string is not provided you will be prompted for it. 
- The search string can include wildcards such as "*" and "?". 
- The search string can be enclosed in quotes for multiple space seperated search criteria. 
### Interactive Usage
The user is prompted for search string (when not provided) and then can specify which domain to search. Once the search results are displayed the user can pull up cdbInfo for the items in the results. 

NOTE: Interactive mode works nicely with `--pager` switch to keep the console clean. 
```sh 
$ cdbSearch --interactive [--pager]
? Search String: CDB test
? Select search domain: Catalog                        
? Continue: Select for details
? Select Item: 2103- 001 - CDB Test Component
```

### Options: 
- `--interactive`
  - Allows user to interactively select domain and select item for details. `--search-domain` is ignored with this option.
- `--search-domain`
  - Use can specify which item domain should be searched. 
  - The following options can be provided: All (Default),Catalog, Cable Catalog, Inventory, Cable Inventory, Machine Design, Cable Design, Location
- `--format`
  - Option will change if the print is displayed in ``rich`` cli format or ``json`` cli format. 
- `--pager`
  - Option will print out results in a paginated form similar to command `less`. 
- `--help` 
  - Option will list all options and arguments. 
## cdbInfo
This CLI allows a user to pull up information about an item in CDB. 
### Usage
The `--qr` or `--id-` input parameter is required and used to speficy the id or qrid of item to fetch info about. 
```sh 
$ cdbInfo --qr=123
$ cdbInfo --id=123
```
### Options: 
- `--id` 
  - Required if qr is not provided.
  - ID of item to fetch information.
- `--qr` 
  - Required if id is not provided.
  - QRID of item to fetch intormation. 
- `--all`
  - Display more information about an item such as:
    - Properties
    - Logs 
    - Domain specific information suchs as inventory, machine install counts, inventory located in a locatation, etc.
- `--log-limit`
  - To be used with `--all` switch. The logs are limited to `5` (default) but the user can increase this limit or specify `-1` for infinite log list. 
- `--inventory-mode`
  - To be used with catalog item with `--all` switch. `full` can be specified for all inventory or `spare` (default) can be specified to only show spare inventory items. 
- `--format`
  - Option will change if the print is displayed in ``rich`` cli format or ``json`` cli format. 
- `--pager`
  - Option will print out results in a paginated form similar to command `less`. 
- `--help` 
  - Option will list all options and arguments. 

## cdb-cli
Broad cdbCli tool that encapsulates all of the CLI functionality under one command. 
### Usage
```sh
$ cdb-cli --help
$ cdb-cli cdb-search # Equivalent to cdbSearch described above.
$ cdb-cli cdb-info # Equivalent to cdbInfo desribed above 
```
### Using a specific command 
```sh
cdb-cli --help # This will list all commands. 
cdb-cli [command-name] --help 
```

### Options
- `--help` 
  - Option will list all cli tools

