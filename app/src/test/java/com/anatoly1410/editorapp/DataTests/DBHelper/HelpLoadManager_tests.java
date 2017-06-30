package com.anatoly1410.editorapp.DataTests.DBHelper;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Data.HelpIndexElement;
import com.anatoly1410.editorapp.Data.HelpLoadManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IFileManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IHelpLoadManager;

import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.InputStream;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by 1 on 08.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class HelpLoadManager_tests {
    final String helpDirectory =  "/sdcard/com.anatoly1410.editorapp/help";
    /**
     * Custom matcher for verifying actual and expected ValueObjects match.
     */
    class HelpIndexElementMatcher extends ArgumentMatcher<HelpIndexElement> {

        private final HelpIndexElement expected;

        public HelpIndexElementMatcher(HelpIndexElement expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object actual) {
            if (actual.getClass() != HelpIndexElement.class) {
                return false;
            }
            HelpIndexElement actualElement = (HelpIndexElement) actual;

            boolean areEqual;
            areEqual = expected.name.equals(actualElement.name)
                    && expected.filePath.equals(actualElement.filePath)
                    && compareChildrenList(expected.children,actualElement.children)
                    && expected.aliases.equals(actualElement.aliases);

            if (actualElement.parent == null || expected.parent == null) {
                if(actualElement.parent ==  expected.parent ){
                    return areEqual;
                }else{
                    return false;
                }
            } else if (actualElement.parent.name.equals(expected.parent.name)) {
                return areEqual;
            } else {
                return false;
            }
        }
        @Override
        public void describeTo(Description description) {
            description.appendText(expected == null ? null : expected.toString());
        }

        private boolean compareChildrenList(ArrayList<HelpIndexElement> childList1,
                                         ArrayList<HelpIndexElement> childList2){
            if(childList1.size() != childList2.size()){
                return false;
            }
            for(int i=0;i<childList1.size();++i){
                if(!childList1.get(i).name.equals(childList2.get(i).name)){
                    return false;
                }
            }
            return true;
        }
    }
    @Test
    public void checkLoadHelpIndex() {
        ClassLoader classLoader = getClass().getClassLoader();
        IFileManager fileManager = mock(FileManager.class);
        HelpLoadManager spyHelpLoadManager = spy(new HelpLoadManager(fileManager));

        InputStream in = classLoader.getResourceAsStream("xml/help_index.xml");

        doReturn(in).when(spyHelpLoadManager).getInputStream(anyString());
        ArrayList<HelpIndexElement> helpIndexElements = spyHelpLoadManager.LoadHelpIndex();

        assertEquals(helpIndexElements.size(),2);
        ArrayList<String> aliases1 = new ArrayList<String>() {{
            add("dd");
            add("func");
        }};
        ArrayList<HelpIndexElement> children2 = new ArrayList<>();

        ArrayList<String> aliases2 = new ArrayList<String>() {{
            add("def");
            add("function");
        }};
        HelpIndexElement el1 = new HelpIndexElement("func1",aliases1,null,"functions2.html",null);
        HelpIndexElement el2 = new HelpIndexElement("functions",aliases2,null,"functions.html",null);
        children2.add(el1);
        el2.children = children2;
        el1.children = new ArrayList<>();
        el1.parent = el2;

        HelpIndexElementMatcher matcher1 = new HelpIndexElementMatcher(el1);
        HelpIndexElementMatcher matcher2 = new HelpIndexElementMatcher(el2);

        assert(matcher1.matches(helpIndexElements.get(0)) && matcher2.matches(helpIndexElements.get(1))
        || matcher1.matches(helpIndexElements.get(1)) && matcher2.matches(helpIndexElements.get(0)));
    }
    @Test
    public void checkLoadHelpIndex_withError() {
        ClassLoader classLoader = getClass().getClassLoader();
        IFileManager fileManager = mock(FileManager.class);
        HelpLoadManager spyHelpLoadManager = spy(new HelpLoadManager(fileManager));
        InputStream in = classLoader.getResourceAsStream("xml/help_index2.xml");
        when(spyHelpLoadManager.getInputStream(anyString())).thenReturn(in);
        ArrayList<HelpIndexElement> helpIndexElements = spyHelpLoadManager.LoadHelpIndex();
        assertEquals(helpIndexElements,null);
    }
    @Test
    public void checkLoadHelpFileContent() {
        IFileManager fileManager = mock(FileManager.class);
        IHelpLoadManager spyHelpLoadManager = spy(new HelpLoadManager(fileManager));
        spyHelpLoadManager.LoadHelpFileContent("file_path");
        String pathExpected = helpDirectory + "/" + "file_path";
        verify(fileManager).loadContent(helpDirectory + "/" + "file_path");
    }
}
