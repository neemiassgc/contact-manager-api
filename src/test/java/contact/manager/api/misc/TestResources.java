package contact.manager.api.misc;

import contact.manager.api.user.User;
import org.springframework.security.oauth2.jwt.Jwt;
import contact.manager.api.contact.Contact;
import contact.manager.api.contact.Address;
import org.mockito.verification.VerificationMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Named.named;
import static org.mockito.Mockito.times;

public final class TestResources {

    private final static List<Contact> contacts = new ArrayList<>();

    private TestResources() {}

    static {
        final User thomas = new User("auth0|86676d5cda7cb5dc3b594f45", "thomas");
        final User joe = new User("auth0|3baa9bc92c9c5decbda32f76", "joe");
        final User robert = new User("auth0|94afd9e7294a59e73e6abfbd", "robert");

        final Contact contact1 = new Contact("Craig Bennett", UUID.randomUUID());
        contact1.setUser(thomas);
        contact1.putPhoneNumber("home", "+44  078 5357 6955");
        contact1.putEmail("main", "ethan.morris@yahoo.co.uk");
        contact1.putAddress("home", Address.builder()
            .street("2062 Maud Street")
            .city("Dollarbeg")
            .state("Delaware")
            .country("United Kindom")
            .zipcode("FK14 9HE").build()
        );

        final Contact contact2 = new Contact("Maisie Harris", UUID.randomUUID());
        contact2.setUser(thomas);
        contact2.putPhoneNumber("office", "+44  077 8877 6574");
        contact2.putEmail("main", "saunders.danielle@allen.info");
        contact2.putAddress("home", Address.builder()
            .country("United Kindom")
            .city("Star")
            .state("Delaware")
            .street("2062 Maud Street")
            .zipcode("KY7 7EY").build()
        );

        final Contact contact3 = new Contact("Lauren Bailey", UUID.randomUUID());
        contact3.setUser(thomas);
        contact3.putPhoneNumber("home", "+44 078 8085 6986");
        contact3.putEmail("main", "yrichards@hotmail.co.uk");
        contact3.putAddress("home", Address.builder()
            .country("United Kindom")
            .street("2062 Maud Street")
            .city("Bont Newydd")
            .state("Delaware")
            .zipcode("LL40 2WX").build()
        );

        final Contact contact4 = new Contact("Greg from accounting", UUID.fromString("5c21433c-3c70-4253-a4b2-52b157be4167"));
        contact4.setUser(joe);
        contact4.putPhoneNumber("office", "+359(26)5948-0427");
        contact4.putEmail("main", "gregfromaccouting@hotmail.co.jp");
        contact4.putAddress("home", Address.builder()
            .street("343-1199, Tennodai")
            .country("Japan")
            .city("Abiko-shi")
            .state("Chiba")
            .zipcode("02169")
            .build()
        );
        contact4.putAddress("work", Address.builder()
            .street("127-1121, Hiyamizu")
            .country("Japan")
            .city("Rankoshi-cho Isoya-gun")
            .state("Hokkaido")
            .zipcode("02169")
            .build()
        );

        final Contact contact5 = new Contact("Coworker Fred", UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408"));
        contact5.setUser(joe);
        contact5.putPhoneNumber("home", "+52(54)6536-5876");
        contact5.putPhoneNumber("mobile", "+81(56)4205-8516");
        contact5.putPhoneNumber("office", "+359(10)4094-9549");
        contact5.putEmail("main", "yuki.fred@gmail.com");
        contact5.putAddress("home", Address.builder()
            .street("4454 Steve Hunt Road")
            .country("EUA")
            .city("Miami")
            .state("Florida")
            .zipcode("33131")
            .build()
        );

        final Contact contact6 = new Contact("Sister Monica", UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7"));
        contact6.setUser(joe);
        contact6.putPhoneNumber("office", "+65(77)4248-0921");
        contact6.putEmail("main", "usermonica01@outlook.com");
        contact6.putAddress("home",  Address.builder()
            .street("4529 Jehovah Drive")
            .country("EUA")
            .city("Waynesboro")
            .state("Virginia")
            .zipcode("22980")
            .build()
        );

        final Contact contact7 = new Contact("Best friend Julia", UUID.fromString("7f23057f-77bd-4568-ac64-e933abae9a09"));
        contact7.setUser(robert);
        contact7.putPhoneNumber("home", "+31(47)1688-3562");
        contact7.putPhoneNumber("office", "+39(05)8263-6323");
        contact7.putPhoneNumber("office2", "+1(22)2514-4183");
        contact7.putEmail("main", "rick.julia@zipmail.com");
        contact7.putEmail("second", "juliarcs@outlook.com");
        contact7.putEmail("third", "contactforjulia@wolf.com");
        contact7.putAddress("home",  Address.builder()
            .street("1116 Mahlon Street")
            .country("EUA")
            .city("Farmington Hills")
            .state("Michigan")
            .zipcode("48335")
            .build()
        );

        final Contact contact8 = new Contact("Mom", UUID.fromString("84edd1b9-89a5-4107-a84d-435676c2b8f5"));
        contact8.setUser(robert);
        contact8.putPhoneNumber("home", "+65(91)6788-9156");
        contact8.putEmail("main", "Sheyla.orton@hoppe.org");
        contact8.putAddress("home",  Address.builder()
            .street("2259 Sycamore Fork Road")
            .country("EUA")
            .city("Hopkins")
            .state("Minnesota")
            .zipcode("55343")
            .build()
        );

        final Contact contact9 = new Contact("Pizza and burgers", UUID.fromString("8fb2bd75-9aec-4cc5-b77b-a95f06081388"));
        contact9.setUser(robert);
        contact9.putPhoneNumber("home", "+81(78)8606-4615");
        contact9.putEmail("main", "pizzaandburgers.main@amazon.com");
        contact9.putEmail("second", "pizzaandburgers.store2@amazon.com");
        contact9.putAddress("store 1",  Address.builder()
            .street("3267 Mercer Street")
            .country("EUA")
            .city("San Diego")
            .state("California")
            .zipcode("92119")
            .build()
        );
        contact9.putAddress("store 2",  Address.builder()
            .street("2644 Arron Smith Drive")
            .country("EUA")
            .city("Thelma")
            .state("Kentucky")
            .zipcode("41260")
            .build()
        );
        contact9.putAddress("store 3",  Address.builder()
            .street("2221 Spruce Drive")
            .country("EUA")
            .city("Core")
            .state("Pennsylvania")
            .zipcode("26529")
            .build()
        );

        final Contact contact10 = new Contact("Uncle Jeff", UUID.fromString("b621650d-4a81-4016-a917-4a8a4992aaef"));
        contact10.setUser(robert);
        contact10.putPhoneNumber("home", "+39(80)9464-0706");
        contact10.putPhoneNumber("mobile", "+31(14)1750-4453");
        contact10.putEmail("main", "contactforjeff.now@yahoo.com");
        contact10.putAddress("home",  Address.builder()
            .street("237-1233, Ichihasama Shimmai")
            .country("Japan")
            .city("Kurihara-shi")
            .state("Miyagi")
            .zipcode("46231")
            .build()
        );
        contact10.putAddress("work",  Address.builder()
            .street("210-1040, Okada")
            .country("Japan")
            .city("Chikushino-shi")
            .state("Fukuoka")
            .zipcode("48335")
            .build()
        );

        contacts.add(contact1);
        contacts.add(contact2);
        contacts.add(contact3);
        contacts.add(contact4);
        contacts.add(contact5);
        contacts.add(contact6);
        contacts.add(contact7);
        contacts.add(contact8);
        contacts.add(contact9);
        contacts.add(contact10);
    }

    public static Contact getContactById(UUID uuid) {
        return contacts.stream().filter(contact -> contact.getId().equals(uuid)).toList().get(0);
    }

    public static Contact getFirstContact() {
        return contacts.get(0);
    }

    public static List<Contact> getAFewContacts(final int count) {
        return contacts.subList(0, count > contacts.size() ? contacts.size() - 1 : count);
    }

    public static List<Contact> getContactsForJoe() {
        return filterContactsByUser("joe");
    }

    public static List<Contact> getContactsForRobert() {
        return filterContactsByUser("robert");
    }

    private static List<Contact> filterContactsByUser(final String username) {
        return contacts
            .stream()
            .filter(it -> it.getUser().getUsername().equals(username))
            .collect(Collectors.toList());
    }

    public static Contact makeCopy(final Contact contactToBeCopied) {
        final Contact newContact = new Contact(contactToBeCopied.getName(), contactToBeCopied.getId());
        newContact.setUser(contactToBeCopied.getUser());
        for (final Map.Entry<String, String> entry : contactToBeCopied.getPhoneNumberMap().entrySet())
            newContact.putPhoneNumber(entry.getKey(), entry.getValue());
        for (final Map.Entry<String, String> entry : contactToBeCopied.getEmailMap().entrySet())
            newContact.putEmail(entry.getKey(), entry.getValue());
        for (final Map.Entry<String, Address> entry : contactToBeCopied.getAddressMap().entrySet()) {
            final Address address = Address.builder()
                .state(entry.getValue().getState())
        .street(entry.getValue().getStreet())
        .city(entry.getValue().getCity())
        .country(entry.getValue().getCountry())
        .zipcode(entry.getValue().getZipcode())
        .build();
        newContact.putAddress(entry.getKey(), address);
        }
        return newContact;
    }

    public static Jwt jwtForJoe() {
        return createJwt("joe").subject(idForJoe()).build();
    }

    public static Jwt jwtForRobert() {
        return createJwt("robert").subject(idForRobert()).build();
    }

    public static Jwt jwtForJulia() {
        return createJwt("julia").subject(idForJulia()).build();
    }

    private static Jwt.Builder createJwt(String username) {
        return Jwt.withTokenValue("{}")
            .claim("username", username)
            .header("alg", "RS256")
            .header("typ", "JWT")
            .header("kid", "Pgj1sRhThSD2fsOc_c6mX");
    }

    public static String idForJoe() {
        return "auth0|3baa9bc92c9c5decbda32f76";
    }

    public static String idForRobert() {
        return "auth0|94afd9e7294a59e73e6abfbd";
    }

    public static String idForJulia() {
        return "auth0|c7b8835b2947d4bcc799dca5";
    }

    public static VerificationMode once() {
        return times(1);
    }

    public static User getMockedUser() {
        return getMockedUser("123408973423", "Jessi");
    }

    public static User getMockedUser(String userId, String username) {
        return new User(userId, username);
    }
}