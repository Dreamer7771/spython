package com.anatoly1410.editorapp.DomainTests;

/**
 * Created by 1 on 08.06.2017.
 */

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.SettingsManager;
import com.anatoly1410.editorapp.Domain.BlockedTextExtractor;
import com.anatoly1410.editorapp.Domain.BlockedTextSpinnerAdapter;
import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;
import com.anatoly1410.editorapp.Domain.SyntaxBlock;
import com.anatoly1410.editorapp.Presentation.Interfaces.IBlockedTextExtractor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BlockedTextExtractor_tests {
    @Test
    public void checkGetBlockHeader_function(){
        ISettingsManager mockSettingsManager = mock(SettingsManager.class);
        BlockedTextExtractor spyBlockExtractor = spy(new BlockedTextExtractor(mockSettingsManager));
        String header = spyBlockExtractor.getBlockHeader("def main():\n pass",BlockedTextExtractor.FUNC_BLOCK_TYPE);
        assertEquals(header,"main()");
    }
    @Test
    public void checkGetBlockHeader_class(){
        ISettingsManager mockSettingsManager = mock(SettingsManager.class);
        BlockedTextExtractor spyBlockExtractor = spy(new BlockedTextExtractor(mockSettingsManager));
        String header = spyBlockExtractor.getBlockHeader("class main:\n pass",BlockedTextExtractor.CLASS_BLOCK_TYPE);

        assertEquals(header,"main");
    }
    @Test
    public void checkUpdateBlocksList(){
        ISettingsManager mockSettingsManager = mock(SettingsManager.class);
        when(mockSettingsManager.getIntSetting(ISettingsManager.APP_PREFERENCES_SORT_ORDER))
                .thenReturn(0);
        when(mockSettingsManager.getBooleanSetting(ISettingsManager.APP_PREFERENCES_SHOW_CLASSES))
                .thenReturn(true);
        when(mockSettingsManager.getBooleanSetting(ISettingsManager.APP_PREFERENCES_SHOW_FUNCTIONS))
                .thenReturn(true);

        BlockedTextExtractor spyBlockExtractor = spy(new BlockedTextExtractor(mockSettingsManager));
        doReturn(true).when(spyBlockExtractor).areBlocksLexSorted();
        doReturn(0).when(spyBlockExtractor).getLastSelectedBlock();
        IBlockedTextExtractor.OnSpinnerSelectedItemChangeListener mockSpinnerSelectedListener
                = mock(IBlockedTextExtractor.OnSpinnerSelectedItemChangeListener.class);
        spyBlockExtractor.setSpinnerSelectedItemChangeListener(mockSpinnerSelectedListener);
        ArrayList<SyntaxBlock> blocks = new ArrayList<>();
        blocks.add(new SyntaxBlock("block2",0,BlockedTextExtractor.FUNC_BLOCK_TYPE));
        blocks.add(new SyntaxBlock("block1",15,BlockedTextExtractor.CLASS_BLOCK_TYPE));
        spyBlockExtractor.setListOfBlocks(blocks);
        BlockedTextSpinnerAdapter mockBlockSpinnerAdapter = mock(BlockedTextSpinnerAdapter.class);
        spyBlockExtractor.setBlockedTextSpinnerAdapter(mockBlockSpinnerAdapter);
        when(mockBlockSpinnerAdapter.getCount()).thenReturn(2);

        spyBlockExtractor.updateBlocksList();
        ArrayList<SyntaxBlock> displayedBlocks = spyBlockExtractor.getDisplayedBlocks();
        assertEquals(displayedBlocks.size(),2);
        assertEquals(displayedBlocks.get(0).header,"block1");
        assertEquals(displayedBlocks.get(0).startPos,15);
        assertEquals(displayedBlocks.get(0).type,BlockedTextExtractor.CLASS_BLOCK_TYPE);
        assertEquals(displayedBlocks.get(1).header,"block2");
        assertEquals(displayedBlocks.get(1).startPos,0);
        assertEquals(displayedBlocks.get(1).type,BlockedTextExtractor.FUNC_BLOCK_TYPE);
        verify(mockSpinnerSelectedListener).fireEvent(0);
    }
    @Test
    public void checkGetMaxDisplayedHeaderLength(){
        ISettingsManager mockSettingsManager = mock(SettingsManager.class);
        BlockedTextExtractor spyBlockExtractor = spy(new BlockedTextExtractor(mockSettingsManager));
        doReturn(10).when(spyBlockExtractor).getMaxDisplayedHeaderLength();

        String reduced = spyBlockExtractor.reduceBlockHeader("very long header");
        assertEquals(reduced,"very long ...");
    }
}
