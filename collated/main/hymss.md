# hymss
###### /java/seedu/address/logic/commands/EditCommand.java
``` java
        public void setBirthday(Birthday birthday) {
            this.birthday = birthday;
        }

        public Optional<Birthday> getBirthday() {
            return Optional.ofNullable(birthday);
        }

```
###### /java/seedu/address/logic/commands/ListBirthdayCommand.java
``` java
/**
 * Lists all persons in Contags whose birthday falls on the current day.
 */
public class ListBirthdayCommand extends Command {

    public static final String COMMAND_WORD = "listbirthday";
    public static final String COMMAND_ALIAS = "lb";

    public static final String MESSAGE_SUCCESS = "Listed all contacts whose birthday is today.\n"
            + "Wish them a happy birthday by using the mail or SMS command!";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all the persons whose birthday is the current day.\n";

    private BirthdayChecker checker = new BirthdayChecker();

    public ListBirthdayCommand (){

    }

    @Override
    public CommandResult execute() {
        model.updateFilteredPersonList(checker);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
```
###### /java/seedu/address/logic/commands/MailCommand.java
``` java
/**
 * Mails a person in Contags.
 */
public class MailCommand extends Command {

    public static final String COMMAND_WORD = "mail";
    public static final String COMMAND_ALIAS = "m";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Mails a contact in Contags.\n"
            + "Recipient mail address cannot be blank.\n"
            + "Parameters: " + PREFIX_MAIL_RECEPIENT + " INDEX"
            + "Example: " + COMMAND_WORD + " " + PREFIX_MAIL_RECEPIENT + "1";

    public static final String MESSAGE_SUCCESS = "Redirecting to Mail application.";
    public static final String MESSAGE_FAILURE = "Could not redirect to Mail application. "
            + "Please enter a valid index and ensure that you have a working internet connection.";

    private final Index targetIndex;

    public MailCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    /**
     * Opens up default Desktop Mail application.
     */

    public void sendMail(String sendMailTo) throws ParseException, IOException, URISyntaxException {

        String host = "localhost";

        Properties properties = System.getProperties();
        properties.setProperty("mail.sftp.host", host);

        Desktop desktop = Desktop.getDesktop();
        URI mailTo;
        String url = "";

        try {
            url = "mailTo:" + sendMailTo;
            mailTo = new URI(url);
            desktop.mail(mailTo);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommandResult execute() throws CommandException {

        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_FAILURE);
        }

        String sendMailTo = lastShownList.get(targetIndex.getZeroBased()).getEmail().toString();

        try {
            sendMail(sendMailTo);
        } catch (ParseException e) {
            e.printStackTrace();
            return new CommandResult(MESSAGE_FAILURE);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof MailCommand // instanceof handles nulls
                && this.targetIndex.equals(((MailCommand) other).targetIndex)); // state check
    }

}
```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java
        case ListBirthdayCommand.COMMAND_WORD:
        case ListBirthdayCommand.COMMAND_ALIAS:
            return new ListBirthdayCommand();

```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java
        case MailCommand.COMMAND_WORD:
        case MailCommand.COMMAND_ALIAS:
            return new MailCommandParser().parse(arguments);

```
###### /java/seedu/address/logic/parser/MailCommandParser.java
``` java
/**
 * Parses input arguments and creates a new MailCommand object
 */
public class MailCommandParser implements Parser<MailCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the MailCommand
     * and returns an MailCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */

    public MailCommand parse(String args) throws ParseException {

        try {
            Index index = ParserUtil.parseIndex(args);
            return new MailCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, MailCommand.MESSAGE_USAGE));
        }

    }
}
```
###### /java/seedu/address/logic/parser/ParserUtil.java
``` java
    /**
     * Parses a {@code Optional<String> birthday} into an {@code Optional<Birthday>} if {@code birthday} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Birthday> parseBirthday(Optional<String> birthday) throws IllegalValueException {
        requireNonNull(birthday);
        return birthday.isPresent() ? Optional.of(new Birthday(birthday.get())) : Optional.empty();
    }

```
###### /java/seedu/address/model/ModelManager.java
``` java
    @Override
    public void updateFilteredPersonList(Predicate<ReadOnlyPerson> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

```
###### /java/seedu/address/model/person/AnyParticularContainsKeywordsPredicate.java
``` java
    @Override
    public boolean test(ReadOnlyPerson person) {

        Set<Tag> personTags = person.getTags();
        String tempAllTagNames = "";
        for (Tag tag: personTags) {
            tempAllTagNames = tempAllTagNames + tag.getTagName() + " ";
        }
        final String allTagNames = tempAllTagNames;

        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword))
                || keywords.stream().anyMatch(keyword -> StringUtil.containsWordIgnoreCase
                (person.getAddress().toString(), keyword))
                || keywords.stream().anyMatch(keyword -> StringUtil.containsWordIgnoreCase
                (person.getBirthday().toString(), keyword))
                || keywords.stream().anyMatch(keyword -> StringUtil.containsWordIgnoreCase
                (person.getEmail().toString(), keyword))
                || keywords.stream().anyMatch(keyword -> StringUtil.containsWordIgnoreCase
                (person.getPhone().toString(), keyword))
                || keywords.stream().anyMatch(keyword -> StringUtil.containsWordIgnoreCase
                (person.getRemark().getRemarkText(), keyword))
                || keywords.stream().anyMatch(keyword -> StringUtil.containsWordIgnoreCase(allTagNames, keyword));
    }

```
###### /java/seedu/address/model/person/Birthday.java
``` java
/**
 * Represents a Person's birthday in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidBirthday(String)}
 */
public class Birthday {

    public static final String MESSAGE_BIRTHDAY_CONSTRAINTS =
            "Birthdays cannot be empty and should be an actual date. They can only contain numbers and forward slashes."
                + " The birthday must be in the form dd/mm/yy or dd/mm/yyy.";

    /*
     * The first character of the birthday must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String BIRTHDAY_VALIDATION_REGEX = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)"
            + "(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3"
            + "(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$"
            + "|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";

    public final String value;

    /**
     * Validates given birthday.
     * @param birthday
     * @throws IllegalValueException if given birthday string is invalid.
     */
    public Birthday(String birthday) throws IllegalValueException {
        requireNonNull(birthday);
        String trimmedBirthday = birthday.trim();
        if (!isValidBirthday(trimmedBirthday)) {
            throw new IllegalValueException(MESSAGE_BIRTHDAY_CONSTRAINTS);
        }
        this.value = birthday;
    }

    /**
     * Returns true if a given string is a valid birthday.
     */
    public static boolean isValidBirthday(String test) {
        return test.matches(BIRTHDAY_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Birthday // instanceof handles nulls
                && this.value.equals(((Birthday) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
```
###### /java/seedu/address/model/person/BirthdayChecker.java
``` java
/**
 * Tests that a {@code ReadOnlyPerson}'s {@code Birthday} matches current date.
 */
public class BirthdayChecker implements Predicate<ReadOnlyPerson> {

    public BirthdayChecker(){

    }

    /**
    * Checks if a contact's birthday falls on the current day.
    * @param person
    * @return boolean
    * @throws ParseException
    */

```
###### /java/seedu/address/model/person/BirthdayChecker.java
``` java
    @Override
    public boolean test(ReadOnlyPerson person) {
        boolean index = false;
        try {
            index = isBirthday(person);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return index;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof BirthdayChecker); // instanceof handles nulls
    }
}
```
###### /java/seedu/address/model/person/Person.java
``` java
    public void setBirthday(Birthday birthday) {
        this.birthday.set(requireNonNull(birthday));
    }

    @Override
    public ObjectProperty<Birthday> birthdayProperty() {
        return birthday;
    }

    @Override
    public Birthday getBirthday() {
        return birthday.get();
    }

```
###### /java/seedu/address/model/tag/Tag.java
``` java
    public String getTagName() {
        return this.tagName;
    }

```
###### /java/seedu/address/storage/AddressBookStorage.java
``` java
    void backupAddressBook(ReadOnlyAddressBook addressBook) throws IOException;

}
```
###### /java/seedu/address/storage/StorageManager.java
``` java
    @Override
    public void backupAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        addressBookStorage.saveAddressBook(addressBook,
                addressBookStorage.getAddressBookFilePath() + "-backup.xml");
    }

```
###### /java/seedu/address/storage/XmlAddressBookStorage.java
``` java
    public void backupAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, filePath.concat("backup.fxml"));
    }

}
```
###### /java/seedu/address/ui/BrowserPanel.java
``` java
    /**
     * Loads the address of the contact selected and corresponding google maps page.
     * @param pers
     */
    private void loadPersonPage(ReadOnlyPerson pers) {
        loadPage(GOOGLE_SEARCH_URL_PREFIX
                + pers.getAddress().value.replaceAll(" ", "+").replaceAll(",", "%2C"));
        setAddress(pers);
        setField("Address");
    }

    /**
     * Loads the address of the contact selected and corresponding google maps page through last saved address.
     * Meant to be called by ToggleCommand.
     */
    private void loadPersonPage() {
        loadPage(GOOGLE_SEARCH_URL_PREFIX + lastAddress.value.replaceAll(" ", "+")
                .replaceAll(",", "%2C"));
        value.setText(lastAddress.value);
        field.setText("Address");
    }

```
###### /resources/view/BrowserPanel.fxml
``` fxml

<StackPane xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
  <VBox fx:id="addressPane" prefHeight="200.0" prefWidth="100.0">
    <children>
      <SplitPane dividerPositions="0.5" maxHeight="50.0" minHeight="50.0" prefHeight="50.0" prefWidth="20.0">
        <items>
          <Label fx:id="field" maxWidth="160.0" minWidth="80.0">
            <padding>
              <Insets left="5.0" />
            </padding></Label>
          <Label fx:id="value" />
        </items>
      </SplitPane>
      <WebView fx:id="browser" minHeight="-Infinity" minWidth="-Infinity" maxHeight="Infinity" VBox.vgrow="ALWAYS" />
    </children>
  </VBox>
</StackPane>
```
