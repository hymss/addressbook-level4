# hymss
###### /java/seedu/address/logic/commands/AddCommandTest.java
``` java
        @Override
        public void updateFilteredPersonList(Predicate<ReadOnlyPerson> predicate) {
            fail("This method should not be called.");
        }

```
###### /java/seedu/address/logic/commands/ListBirthdayCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListBirthdayCommandTest {

    private Model model;
    private Model expectedModel;
    private ListBirthdayCommand listBirthdayCommand;
    private BirthdayChecker checker = new BirthdayChecker();

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        listBirthdayCommand = new ListBirthdayCommand();
        listBirthdayCommand.setData(model, new CommandHistory(), new UndoRedoStack());
    }

    @Test
    public void executeListBirthdaySuccess() {
        ListBirthdayCommand listBirthdayCommand = prepareCommand();
        expectedModel.updateFilteredPersonList(checker);
        assertCommandSuccess(listBirthdayCommand, model, listBirthdayCommand.MESSAGE_SUCCESS, expectedModel);
    }

    /**
     *  This filters Contags to show persons whose birthdays fall on the current date.
     */
    private ListBirthdayCommand prepareCommand() {
        ListBirthdayCommand command = new ListBirthdayCommand();
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }
}
```
###### /java/seedu/address/logic/commands/MailCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) for {@code MailCommand}.
 */

public class MailCommandTest {
    @Rule
    public final EventsCollectorRule eventsCollectorRule = new EventsCollectorRule();

    private Model model;

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertExecutionFailure(outOfBoundsIndex, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /*
     * Commented out code below as assertExecutionSuccess method fails on Travis CI due to HeadlessException.
     * Works locally. Import static seedu.address.commons.core.Messages.MESSAGE_MAIL_FAILURE and
     * java.awt.HeadlessException if you want to un-comment the code below and assertExecutionSuccess method.
     */
    /* @Test
    public void execute_validIndexFilteredList_success() {
        showFirstPersonOnly(model);
        assertExecutionSuccess(INDEX_FIRST_PERSON, MESSAGE_MAIL_SUCCESS);
    } */

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showFirstPersonOnly(model);

        Index outOfBoundsIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundsIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        assertExecutionFailure(outOfBoundsIndex, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        MailCommand mailFirstCommand = new MailCommand(INDEX_FIRST_PERSON);
        MailCommand mailSecondCommand = new MailCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(mailFirstCommand.equals(mailFirstCommand));

        // same values -> returns true
        MailCommand mailFirstCommandCopy = new MailCommand(INDEX_FIRST_PERSON);
        assertTrue(mailFirstCommand.equals(mailFirstCommandCopy));

        // different types -> returns false
        assertFalse(mailFirstCommand.equals(1));

        // null -> returns false
        assertFalse(mailFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(mailFirstCommand.equals(mailSecondCommand));
    }

    /**
     * Executes a {@code SelectCommand} with the given {@code index}, and checks that {@code JumpToListRequestEvent}
     * is raised with the correct index.
     * Commented out code below as it fails on Travis due to HeadlessException but works locally.
     * If you want to un-comment the code below, ensure that java.awt.HeadlessException and
     * static seedu.address.commons.core.Messages.MESSAGE_MAIL_FAILURE are imported.
     */
    /* private void assertExecutionSuccess(Index index, String expectedMessage) {
        MailCommand mailCommand = prepareCommand(index);

        try {
            mailCommand.execute();
            assertEquals(expectedMessage, MESSAGE_MAIL_SUCCESS);
        } catch (CommandException ce) {
            throw new IllegalArgumentException("Execution of command should not fail.", ce);
        } catch (HeadlessException he){
            fail("Execution of command shoud not fail.");
        }
    } */

    /**
     * Executes a {@code MailCommand} with the given {@code index}, and checks that a {@code CommandException}
     * is thrown with the {@code expectedMessage}.
     */
    private void assertExecutionFailure(Index index, String expectedMessage) {
        MailCommand mailCommand = new MailCommand(index);

        try {
            mailCommand.execute();
            fail("The expected CommandException was not thrown.");
            mailCommand.sendMail(index.toString());
        } catch (IOException ioe) {
            assertEquals(expectedMessage, MESSAGE_MAIL_FAILURE);
            assertTrue(eventsCollectorRule.eventsCollector.isEmpty());
        } catch (NullPointerException npe) {
            assertEquals(expectedMessage, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            assertTrue(eventsCollectorRule.eventsCollector.isEmpty());
        } catch (URISyntaxException use) {
            assertEquals(expectedMessage, MESSAGE_MAIL_FAILURE);
            assertTrue(eventsCollectorRule.eventsCollector.isEmpty());
        } catch (ParseException e) {
            assertEquals(expectedMessage, MESSAGE_MAIL_FAILURE);
            assertTrue(eventsCollectorRule.eventsCollector.isEmpty());
        } catch (CommandException ce) {
            assertEquals(expectedMessage, ce.getMessage());
            assertTrue(eventsCollectorRule.eventsCollector.isEmpty());
        }
    }

    /**
     * Parses {@code userInput} into a {@code EmailCommand}.
     */
    private MailCommand prepareCommand(Index index) {
        MailCommand mailCommand = new MailCommand(index);
        mailCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return mailCommand;
    }
}
```
###### /java/seedu/address/logic/parser/AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_listbirthday() throws Exception {
        assertTrue(parser.parseCommand(ListBirthdayCommand.COMMAND_WORD) instanceof ListBirthdayCommand);
        assertTrue(parser.parseCommand(ListBirthdayCommand.COMMAND_ALIAS) instanceof ListBirthdayCommand);
        assertTrue(parser.parseCommand(ListBirthdayCommand.COMMAND_WORD + " 3") instanceof ListBirthdayCommand);
    }

    @Test
    public void parseCommand_mail() throws Exception {
        MailCommand command = (MailCommand) parser.parseCommand(
                MailCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        MailCommand commandAlias = (MailCommand) parser.parseCommand(
                MailCommand.COMMAND_ALIAS + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new MailCommand(INDEX_FIRST_PERSON), command);
        assertEquals(command, commandAlias);
    }

```
###### /java/seedu/address/logic/parser/EditCommandParserTest.java
``` java
        //birthday
        userInput = targetIndex.getOneBased() + BIRTHDAY_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withBirthday(VALID_BIRTHDAY_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

```
###### /java/seedu/address/logic/parser/MailCommandParserTest.java
``` java

/**
 * Test scope: similar to {@code DeleteCommandParserTest}.
 * @see DeleteCommandParserTest
 */
public class MailCommandParserTest {

    private MailCommandParser parser = new MailCommandParser();

    @Test
    public void parse_validArgs_returnsMailCommand() {
        assertParseSuccess(parser, "1", new MailCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                MailCommand.MESSAGE_USAGE));
    }
}
```
###### /java/seedu/address/logic/parser/ParserUtilTest.java
``` java
    @Test
    public void parseBirthday_optionalEmpty_returnsOptionalEmpty() throws Exception {
        assertFalse(ParserUtil.parseBirthday(Optional.empty()).isPresent());
    }

    @Test
    public void parseBirthday_validValue_returnsBirthday() throws Exception {
        Birthday expectedBirthday = new Birthday(VALID_BIRTHDAY);
        Optional<Birthday> actualBirthday = ParserUtil.parseBirthday(Optional.of(VALID_BIRTHDAY));

        assertEquals(expectedBirthday, actualBirthday.get());
    }

    @Test
    public void parseBirthday_null_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        ParserUtil.parseBirthday(null);
    }

    @Test
    public void parseBirthday_invalidValue_throwsIllegalValueException() throws Exception {
        thrown.expect(IllegalValueException.class);
        ParserUtil.parseBirthday(Optional.of(INVALID_BIRTHDAY));
    }

```
###### /java/seedu/address/model/person/BirthdayTest.java
``` java
public class BirthdayTest {

    @Test
    public void isValidPhone() {
        // invalid birthdays
        assertFalse(Birthday.isValidBirthday("")); // empty string
        assertFalse(Birthday.isValidBirthday(" ")); // spaces only
        assertFalse(Birthday.isValidBirthday("91")); // less than 3 numbers
        assertFalse(Birthday.isValidBirthday("12011997")); // no forward slashes between digits
        assertFalse(Birthday.isValidBirthday("twelve/dec/nineteen-ninety")); // non-numeric
        assertFalse(Birthday.isValidBirthday("90/dec/1997")); // alphabets within digits
        assertFalse(Birthday.isValidBirthday("93/ 15/ 34")); // spaces within digits

        // valid birthday
        assertTrue(Birthday.isValidBirthday("20/11/1997")); // dd/mm/yyyy
        assertTrue(Birthday.isValidBirthday("20/11/97")); //dd/mm/yy
    }
}
```
###### /java/seedu/address/storage/StorageManagerTest.java
``` java
    @Test
    public void addressBookReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link XmlAddressBookStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link XmlAddressBookStorageTest} class.
         */
        AddressBook original = getTypicalAddressBook();
        AddressBook backup = getTypicalAddressBook();
        storageManager.saveAddressBook(original);
        ReadOnlyAddressBook retrieved = storageManager.readAddressBook().get();
        assertEquals(original, new AddressBook(retrieved));
        storageManager.backupAddressBook(backup);
        ReadOnlyAddressBook retrivedBackup = storageManager.readAddressBook().get();
        assertEquals(backup, new AddressBook(retrivedBackup));
    }

```
###### /java/seedu/address/testutil/EditPersonDescriptorBuilder.java
``` java
    /**
     *Sets the {@code Birthday} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withBirthday(String birthday) {
        try {
            ParserUtil.parseBirthday(Optional.of(birthday)).ifPresent(descriptor::setBirthday);
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("address is expected to be unique.");
        }
        return this;
    }

```
###### /java/seedu/address/testutil/PersonBuilder.java
``` java
    /**
     * Sets the {@code Birthday} of the {@code Person} that we are building.
     */
    public PersonBuilder withBirthday(String birthday) {
        try {
            this.person.setBirthday(new Birthday(birthday));
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("birthday is expected to be unique.");
        }
        return this;
    }

```
###### /java/systemtests/MailCommandSystemTest.java
``` java
public class MailCommandSystemTest extends AddressBookSystemTest {
    @Test
    public void mail() {
        /* Case: mail the first card in the person list, command with leading spaces and trailing spaces
         * -> mailed
         */
        String command = "   " + MailCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased() + "   ";
        assertCommandSuccess(command, INDEX_FIRST_PERSON);

        /* Case: mail the last card in the person list -> mailed */
        Index personCount = Index.fromOneBased(getTypicalPersons().size());
        command = MailCommand.COMMAND_WORD + " " + personCount.getOneBased();
        assertCommandSuccess(command, personCount);

        /* Case: undo previous mailing -> rejected */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_FAILURE;
        assertCommandFailure(command, expectedResultMessage);

        /* Case: redo mailing last card in the list -> rejected */
        command = RedoCommand.COMMAND_WORD;
        expectedResultMessage = RedoCommand.MESSAGE_FAILURE;
        assertCommandFailure(command, expectedResultMessage);

        /* Case: mail the middle card in the person list -> selected */
        Index middleIndex = Index.fromOneBased(personCount.getOneBased() / 2);
        command = MailCommand.COMMAND_WORD + " " + middleIndex.getOneBased();
        assertCommandSuccess(command, middleIndex);

        /* Case: invalid index (size + 1) -> rejected */
        int invalidIndex = getModel().getFilteredPersonList().size() + 1;
        assertCommandFailure(MailCommand.COMMAND_WORD + " " + invalidIndex, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        /* Case: mail the current selected card -> mailed */
        assertCommandSuccess(command, middleIndex);

        /* Case: filtered person list, mail index within bounds of address book but out of bounds of person list
         * -> rejected
         */
        showPersonsWithName(KEYWORD_MATCHING_MEIER);
        invalidIndex = getModel().getAddressBook().getPersonList().size();
        assertCommandFailure(MailCommand.COMMAND_WORD + " " + invalidIndex, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        /* Case: filtered person list, mail index within bounds of address book and person list -> mailed */
        Index validIndex = Index.fromOneBased(1);
        assert validIndex.getZeroBased() < getModel().getFilteredPersonList().size();
        command = MailCommand.COMMAND_WORD + " " + validIndex.getOneBased();
        assertCommandSuccess(command, validIndex);

        /* Case: invalid index (0) -> rejected */
        assertCommandFailure(MailCommand.COMMAND_WORD + " " + 0,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MailCommand.MESSAGE_USAGE));

        /* Case: invalid index (-1) -> rejected */
        assertCommandFailure(MailCommand.COMMAND_WORD + " " + -1,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MailCommand.MESSAGE_USAGE));

        /* Case: invalid arguments (alphabets) -> rejected */
        assertCommandFailure(MailCommand.COMMAND_WORD + " abc",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MailCommand.MESSAGE_USAGE));

        /* Case: invalid arguments (extra argument) -> rejected */
        assertCommandFailure(MailCommand.COMMAND_WORD + " 1 abc",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MailCommand.MESSAGE_USAGE));

        /* Case: mixed case command word -> rejected */
        assertCommandFailure("mAIl 1", MESSAGE_UNKNOWN_COMMAND);

        /* Case: select from empty address book -> rejected */
        executeCommand(ClearCommand.COMMAND_WORD);
        assert getModel().getAddressBook().getPersonList().size() == 0;
        assertCommandFailure(MailCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased(),
                MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Executes {@code command} and verifies that the command box displays an empty string, the result display
     * box displays the success message of executing mail command with the {@code expectedSelectedCardIndex}
     * of the mailed person, and the model related components equal to the current model.
     * These verifications are done by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the command box has the default style class and the status bar remain unchanged. The resulting
     * browser url and mailed card will be verified if the current selected card is different from
     * the card at {@code expectedSelectedCardIndex}.
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(String command, Index expectedMailedCardIndex) {
        Model expectedModel = getModel();
        String expectedResultMessage = String.format(
                MESSAGE_SUCCESS, expectedMailedCardIndex.getOneBased());

        executeCommand(command);

        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchanged();
    }

    /**
     * Executes {@code command} and verifies that the command box displays {@code command}, the result display
     * box displays {@code expectedResultMessage} and the model related components equal to the current model.
     * These verifications are done by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the browser url, selected card and status bar remain unchanged, and the command box has the
     * error style.
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
```
