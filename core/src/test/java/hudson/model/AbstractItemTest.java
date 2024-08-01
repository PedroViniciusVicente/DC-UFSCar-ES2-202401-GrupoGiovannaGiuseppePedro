package hudson.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;


import java.io.IOException;
import java.util.Collection;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;

import static org.mockito.Mockito.*;
import java.io.File;
import org.junit.Before;

/**
 * @author kingfai
 */
public class AbstractItemTest {

    private static class StubAbstractItem extends AbstractItem {

        protected StubAbstractItem() {
            // sending in null as parent as I don't care for my current tests
            super(null, "StubAbstractItem");
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Collection<? extends Job> getAllJobs() {
            return null;
        }

        /**
         * Override save so that nothing happens when setDisplayName() is called
         */
        @Override
        public void save() {

        }
    }

    @Test
    public void testSetDisplayName() throws Exception {
        final String displayName = "testDisplayName";
        StubAbstractItem i = new StubAbstractItem();
        i.setDisplayName(displayName);
        assertEquals(displayName, i.getDisplayName());
    }

    @Test
    public void testGetDefaultDisplayName() {
        final String name = "the item name";
        StubAbstractItem i = new StubAbstractItem();
        i.doSetName(name);
        // assert that if the displayname is not set, the name is actually returned
        assertEquals(name,  i.getDisplayName());

    }

    @Test
    public void testSearchNameIsName() {
        final String name = "the item name jlrtlekjtekrjkjr";
        StubAbstractItem i = new StubAbstractItem();
        i.doSetName(name);

        assertEquals(i.getName(),  i.getSearchName());
    }

    @Test
    public void testGetDisplayNameOrNull() throws Exception {
        final String projectName = "projectName";
        final String displayName = "displayName";
        StubAbstractItem i = new StubAbstractItem();
        i.doSetName(projectName);
        assertEquals(projectName, i.getName());
        assertNull(i.getDisplayNameOrNull());

        i.setDisplayName(displayName);
        assertEquals(displayName, i.getDisplayNameOrNull());
    }

    @Test
    public void testSetDisplayNameOrNull() throws Exception {
        final String projectName = "projectName";
        final String displayName = "displayName";
        StubAbstractItem i = new StubAbstractItem();
        i.doSetName(projectName);
        assertNull(i.getDisplayNameOrNull());

        i.setDisplayNameOrNull(displayName);
        assertEquals(displayName, i.getDisplayNameOrNull());
        assertEquals(displayName, i.getDisplayName());
    }

    private static class NameNotEditableItem extends AbstractItem {

        protected NameNotEditableItem(ItemGroup parent, String name) {
            super(parent, name);
        }

        @Override
        public Collection<? extends Job> getAllJobs() {
            return null;
        }

        @Override
        public boolean isNameEditable() {
            return false; //so far it's the default value, but it's good to be explicit for test.
        }
    }

    @Test
    @Issue("JENKINS-58571")
    public void renameMethodShouldThrowExceptionWhenNotIsNameEditable() {

        //GIVEN
        NameNotEditableItem item = new NameNotEditableItem(null, "NameNotEditableItem");

        //WHEN
        final IOException e = assertThrows("An item with isNameEditable false must throw exception when trying to rename it.",
                IOException.class, () -> item.renameTo("NewName"));

        assertEquals("Trying to rename an item that does not support this operation.", e.getMessage());
        assertEquals("NameNotEditableItem", item.getName());
    }

    @Test
    @Issue("JENKINS-58571")
    public void doConfirmRenameMustThrowFormFailureWhenNotIsNameEditable() {

        //GIVEN
        NameNotEditableItem item = new NameNotEditableItem(null, "NameNotEditableItem");

        //WHEN
        final Failure f = assertThrows("An item with isNameEditable false must throw exception when trying to call doConfirmRename.",
                Failure.class, () -> item.doConfirmRename("MyNewName"));
        assertEquals("Trying to rename an item that does not support this operation.", f.getMessage());
        assertEquals("NameNotEditableItem", item.getName());
    }

    private static class GenericItem extends AbstractItem {

        protected GenericItem(ItemGroup parent, String name) {
            super(parent, name);
        }

        @Override
        public Collection<? extends Job> getAllJobs() {
            return null;
        }

        @Override
        public boolean isNameEditable() {
            return false; //so far it's the default value, but it's good to be explicit for test.
        }
    }

    @Test
    //@Issue("JENKINS-58571")
    public void excessoDeTempoDevidoSleep() {

        // GIVEN
        NameNotEditableItem item = new NameNotEditableItem(null, "NameNotEditableItem");
        NameNotEditableItem item2 = new NameNotEditableItem(null, "NameNotEditableItem2");
        NameNotEditableItem item3 = new NameNotEditableItem(null, "NameNotEditableItem2");
        NameNotEditableItem item4 = new NameNotEditableItem(null, "NameNotEditableItem2");
        NameNotEditableItem item5 = new NameNotEditableItem(null, "NameNotEditableItem2");

        // WHEN
        try {
            item.renameTo("NewName");
            item2.renameTo("NewName2");
            item3.renameTo("NewName3");
            item4.renameTo("NewName4");
            item5.renameTo("NewName5");
        } catch (IOException e) {
            // THEN
            assertEquals("Trying to rename an item that does not support this operation.", e.getMessage());
            assertEquals("NameNotEditableItem", item.getName());
        }
        System.out.println("Fim do teste");
    }

    /*private static class meuStubAbstractItem extends AbstractItem {

        protected meuStubAbstractItem(ItemGroup parent, String name) {
            super(parent, name);
        }

        @Override
        public Collection<? extends Job> getAllJobs() {
            return null;
        }

        @Override
        public void save() {
            // No-op
        }

        @Override
        public File getRootDir() {  // Note the change to public visibility
            return new File("fake-root-dir");
        }

        // Mocking the renameFile method to simulate renaming behavior
        public boolean renameFile(File oldRoot, File newRoot) {
            return oldRoot.renameTo(newRoot);
        }
    }

    private meuStubAbstractItem item;

    @Before
    public void setUp() {
        item = spy(new meuStubAbstractItem(null, "meuStubAbstractItem"));
    }

    @Test
    public void meuexcessoDeTempoDevidoSleep() throws IOException {
        meuStubAbstractItem item = spy(new meuStubAbstractItem(null, "meuStubAbstractItem"));
        doReturn(true).when(item).isNameEditable();
        doReturn(new File("fake-old-root-dir")).when(item).getRootDir();

        // Mock the renameFile method to fail the first few times
        File newRoot = new File("fake-new-root-dir");
        doReturn(false, false, false, false, true).when(item).renameFile(any(File.class), eq(newRoot));

        long startTime = System.currentTimeMillis();

        item.renameTo("NewName");

        long endTime = System.currentTimeMillis();

        assertTrue("Expected at least 2000ms to have passed", (endTime - startTime) >= 2000);

        verify(item, times(1)).doSetName("NewName");
    }*/

}
