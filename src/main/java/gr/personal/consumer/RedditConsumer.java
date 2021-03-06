package gr.personal.consumer;

import gr.personal.consumer.model.Thing;
import gr.personal.consumer.request.CommentRequest;
import gr.personal.consumer.request.FullnamesRequest;
import gr.personal.consumer.request.PostRequest;
import gr.personal.consumer.request.RedditRequest;
import gr.personal.oauth.Authentication;
import gr.personal.utils.JSONArrayUtils;
import gr.personal.utils.ModelsUtils;
import gr.personal.utils.RedditAPIUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nkanakis on 7/13/2017.
 */
@Component
/**
 * Describes the list of available strategies for consuming content from Reddit's API.
 */
public class RedditConsumer {
    @Autowired
    private Logger logger;
    @Autowired
    private RedditAPIClient client;
    @Autowired
    private Authentication authentication;
    /**
     * Retrieve the first post for specified subreddit.
     *
     * @param subreddit : subreddit to retrieve latest model from.
     * @return (Reddit models) single child object
     */
    public JSONArray fetchInitialPost(String subreddit) {
        logger.debug("Fetch initial post for subreddit: "+ subreddit);
        RedditRequest request = new PostRequest(subreddit, 1, authentication);
        return client.executeGetRequestWithDelayPolicy(request);
    }

    /**
     * Retrieve the first comment for specified subreddit.
     *
     * @param subreddit : subreddit to retrieve latest model from.
     * @return (Reddit models) single child object
     */
    public JSONArray fetchInitialComment(String subreddit) {
        logger.debug("Fetch initial comment for subreddit: "+ subreddit);
        RedditRequest request = new CommentRequest(subreddit, 1, authentication);
        return client.executeGetRequestWithDelayPolicy(request);
    }

    /**
     * Retrieve last RedditAPIUtils.MAX_MODELS_LIMIT(=100) posts
     *
     * @param subreddit : subreddit to retrieve latest models from
     * @return (Reddit models) children
     */
    public JSONArray fetchReversedPosts(String subreddit) {
        logger.debug("Reversed fetch posts for subreddit: "+ subreddit);
        RedditRequest request = new PostRequest(subreddit, authentication);
        return client.executeGetRequestWithDelayPolicy(request);
    }

    /**
     * Retrieve last RedditAPIUtils.MAX_MODELS_LIMIT(=100) comments
     *
     * @param subreddit : subreddit to retrieve latest models from.
     * @return (Reddit models) children
     */
    public JSONArray fetchReversedComments(String subreddit) {
        logger.debug("Reversed fetch comments for subreddit: "+ subreddit);
        RedditRequest request = new CommentRequest(subreddit, authentication);
        return client.executeGetRequestWithDelayPolicy(request);
    }

    /**
     * The method is used to retrieve specific range of fullnames. Starting from (but not included initialFullname)
     * and for the next "length" IDs. eg: if initialFullname = t1_dkio3q5 and length = 3 the method will return:
     * > t1_dkio3q6
     * > t1_dkio3q7
     * > t1_dkio3q8
     * <p>
     *
     * @param initialFullname : starting point
     * @param length          :
     * @return (Reddit models) children
     */
    public JSONArray fetchByFullnames(String initialFullname, int length) {
        logger.debug("Fetch by fullnames for: "+ initialFullname);
        String commaSeparatedFullnames = RedditAPIUtils.transformFullnamesToCommaSeparated(initialFullname, length);
        RedditRequest request = new FullnamesRequest(authentication, commaSeparatedFullnames);
        return client.executeGetRequestWithDelayPolicy(request);
    }


    public JSONArray fetchForward(String initialFullname) {
        return fetchByFullnames(initialFullname, RedditAPIUtils.MAX_MODELS_LIMIT);
    }

    public JSONArray fetchByRange(Thing start, Thing end) {

        long initialId2Decimal = Long.parseLong(start.getId(), 36);
        long finalId2Decimal = Long.parseLong(end.getId(), 36);

        String startFullname = start.getFullName();
        JSONArray concatenatedChildren = new JSONArray();

        while (initialId2Decimal < finalId2Decimal) {
            int range = Math.min(RedditAPIUtils.MAX_MODELS_LIMIT, Math.toIntExact(finalId2Decimal - initialId2Decimal));

            /** fetchByFullnames(startFullname, range) returns (startFullname+1, startFullname+2, .....), in this case
             * we want ((startFullname, startFullname+1, .....)) so we decrease the start full name by one.
             **/
            JSONArray currentChildren = fetchByFullnames(ModelsUtils.decreaseFullnameByOne(startFullname), range);
            String lastFullname = ModelsUtils.extractLastFullname(currentChildren);

            if (lastFullname == null || lastFullname == "")
                continue;

            startFullname = lastFullname;
            initialId2Decimal += range;
            concatenatedChildren = JSONArrayUtils.concatArray(concatenatedChildren, currentChildren);
        }
        return concatenatedChildren;
    }
}
