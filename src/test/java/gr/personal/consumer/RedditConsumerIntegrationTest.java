package gr.personal.consumer;

import gr.personal.consumer.model.Thing;
import gr.personal.helper.IntegrationTest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by nkanakis on 7/20/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Category(IntegrationTest.class)
public class RedditConsumerIntegrationTest {

    @Autowired
    private RedditConsumer redditConsumer;

    @Test
    public void testFetchInitialPost() throws Exception {
        JSONArray children = redditConsumer.fetchInitialPost("all");
        Assert.assertFalse(children.isNull(0));
        JSONObject innerData =  children.getJSONObject(0).getJSONObject("data");
        Assert.assertNotNull(innerData.get("id"));
    }

    @Test
    public void testFetchInitialComment() throws Exception {
        JSONArray children =  redditConsumer.fetchInitialComment("all");
        Assert.assertFalse(children.isNull(0));
        JSONObject innerData =  children.getJSONObject(0).getJSONObject("data");
        Assert.assertNotNull(innerData.get("id"));

    }

    @Test
    public void testFetchReversedPosts() throws Exception {
        JSONArray children =  redditConsumer.fetchReversedPosts("all");
        Assert.assertFalse(children.isNull(99));
        JSONObject innerData =  children.getJSONObject(99).getJSONObject("data");
        Assert.assertNotNull(innerData.get("id"));
    }

    @Test
    public void testTetchReversedComments() throws Exception {
        JSONArray children =  redditConsumer.fetchReversedComments("all");
        Assert.assertFalse(children.isNull(99));
        JSONObject innerData =  children.getJSONObject(99).getJSONObject("data");
        Assert.assertNotNull(innerData.get("id"));
    }

    @Test
    public void testFetchFullnames() throws Exception {
        JSONArray children=  redditConsumer.fetchByFullnames("t1_dkijto7", 2);

        Assert.assertFalse(children.isNull(0));
        JSONObject innerData =  children.getJSONObject(0).getJSONObject("data");
        Assert.assertEquals("dkijto8", innerData.get("id"));

        Assert.assertFalse(children.isNull(1));
        JSONObject innerData2 =  children.getJSONObject(1).getJSONObject("data");
        Assert.assertEquals("dkijto9", innerData2.get("id"));

    }

    @Test
    public void testFetchForward() throws Exception {
        JSONArray children =  redditConsumer.fetchForward("t1_dkijto7");
        Assert.assertFalse(children.isNull(99));

        Assert.assertFalse(children.isNull(0));
        JSONObject innerData =  children.getJSONObject(0).getJSONObject("data");
        Assert.assertEquals("dkijto8", innerData.get("id"));

        Assert.assertFalse(children.isNull(1));
        JSONObject innerData2 =  children.getJSONObject(1).getJSONObject("data");
        Assert.assertEquals("dkijto9", innerData2.get("id"));
    }

    @Test
    public void testFetchByRange() throws Exception{

        JSONArray results = redditConsumer.fetchByRange(new Thing("t1_dkijto7"), new Thing("t1_dkijtts"));
        Assert.assertTrue(results.length()>200);
        JSONObject lastModel = results.getJSONObject(results.length() - 1);
        Assert.assertEquals("t1_dkijtts",lastModel.getJSONObject("data").getString("name") );
    }
}
