package com.anatoly1410.editorapp.Presentation;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;

import com.anatoly1410.editorapp.Domain.AutocompletionManager;
import com.anatoly1410.editorapp.Domain.Snippet;
import com.anatoly1410.editorapp.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import static android.app.PendingIntent.getActivity;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

/**
 * Created by 1 on 10.06.2017.
 */
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);
    @NonNull
    public static Matcher<Object> withSnippet(final Matcher<String> stringMatcher) {

        return new BoundedMatcher<Object, Snippet>(Snippet.class) {

            @Override
            public void describeTo(final Description description) {
                description.appendText("with error text: ");
                stringMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(final Snippet snippet) {
                return  stringMatcher.matches(snippet.tag);
            }
        };
    }
    @Test
    public void autocompletionTest(){
        onView(withId(R.id.mainEdit)).perform(typeText("i"));
        //onData(anything()).inAdapterView(withId(R.id.autocompMenu)).atPosition(0).perform(click());
      //  onData(withSnippet(equalTo("import"))).inAdapterView(withId(R.id.autocompMenu)).perform(scrollTo(),click());
    //    onView(withId(R.id.codeEditText)).check(matches(withText("import ")));
    }
}