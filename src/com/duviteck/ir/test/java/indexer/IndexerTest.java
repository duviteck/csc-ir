package indexer;

import org.junit.Test;

/**
 * Created by duviteck. 27 Sep 2014.
 */
public class IndexerTest {
    @Test
    public void testDocs() {
        IndexerRunner.main(new String[] {"docs", "index.inv"});
    }

    @Test
    public void testByWeb() {
        IndexerRunner.main(new String[] {"/Users/duviteck/Desktop/By.web", "by_web_index.inv"});
    }

    @Test
    public void testByWebShort() {
        IndexerRunner.main(new String[] {"/Users/duviteck/Desktop/By.web.short", "by_web_short_index.inv"});
    }
}
