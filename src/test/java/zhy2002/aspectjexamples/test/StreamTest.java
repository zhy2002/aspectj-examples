package zhy2002.aspectjexamples.test;

import org.junit.Test;
import zhy2002.aspectjexamples.domain.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * Tests for the Java 8 stream feature.
 */
public class StreamTest {

    @Test
    public void canChainConsumers() {

        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();

        Consumer<String> addToList1 = list1::add;
        Consumer<String> addToList2 = list2::add;
        Consumer<String> addToBothLists = addToList1.andThen(addToList2);

        List<String> source = Arrays.asList("v1", "v2", "v3");
        source.forEach(addToBothLists);

        assertThat(list1, equalTo(source));
        assertThat(list2, equalTo(source));
    }

    @Test
    public void canFilterStream(){

        List<Member> members = createSampleMembers();
        List<Member> oldMembers = new ArrayList<>();
        members.stream().filter(member -> member.getAge() >= 25).forEach(oldMembers::add);

        assertThat(oldMembers, hasSize(2));

    }

    @Test
    public void canUseBooleanOperatorsInFiltering(){
        List<Member> members = createSampleMembers();
        List<Member> selectedMembers = new ArrayList<>();
        Predicate<Member> oldMembers = member -> member.getAge() >= 25;
        Predicate<Member> isJoker = member -> member.getName().compareTo("joker") == 0;

        members.stream().filter(oldMembers.or(isJoker)).forEach(selectedMembers::add);

        assertThat(selectedMembers, hasSize(3));
    }

    private List<Member> createSampleMembers(){
        return Arrays.asList(new Member("j1", 21), new Member("j2", 22), new Member("joker", 22), new Member("j3", 23), new Member("queen", 23), new Member("queen", 24), new Member("j4", 24), new Member("j5", 25), new Member("j6", 26));
    }



}
