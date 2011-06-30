package ee.hansa.android;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Meelis Saluvee
 */
public class ContactInfo {
  private String name;
  private Collection<String> numbers = new ArrayList<String>();

  public ContactInfo(String name) {
    this.name = name;
  }

  public ContactInfo addPhoneNumber(String number) {
    numbers.add(number.replace("-", "").replace("+372", ""));
    return this;
  }

  public String getName() {
    return name;
  }

  public String[] getPhoneNumbers() {
    return numbers.toArray(new String[numbers.size()]);
  }
}
