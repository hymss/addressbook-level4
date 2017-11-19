package seedu.address.model.person;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Predicate;

//@@author hymss
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

    //@@author
    public boolean isBirthday(ReadOnlyPerson person) throws ParseException {
        String birthday = person.getBirthday().toString();
        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(birthday);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return (((calendar.get(Calendar.MONTH)) == Calendar.getInstance().get(Calendar.MONTH))
                && ((calendar.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH))));
    }

    //@@author hymss
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
