import java.util.*;

class Twitter {

    private static int timestamp = 0; // global counter to order tweets

    // Each user has a set of people they follow
    private Map<Integer, Set<Integer>> follows;

    // Each user has a list of their tweets
    private Map<Integer, List<Tweet>> tweets;

    // Helper class for tweets (time + id)
    private static class Tweet {
        int time;
        int id;

        Tweet(int time, int id) {
            this.time = time;
            this.id = id;
        }
    }

    public Twitter() {
        follows = new HashMap<>();
        tweets = new HashMap<>();
    }

    public void postTweet(int userId, int tweetId) {
        timestamp++;
        Tweet tweet = new Tweet(timestamp, tweetId);

        tweets.putIfAbsent(userId, new ArrayList<>());
        tweets.get(userId).add(tweet);
    }

    public List<Integer> getNewsFeed(int userId) {
        // max heap based on tweet time
        PriorityQueue<Tweet> maxHeap = new PriorityQueue<>(
                (a, b) -> b.time - a.time
        );

        // add this user's own tweets
        if (tweets.containsKey(userId)) {
            maxHeap.addAll(tweets.get(userId));
        }

        // add followees' tweets
        if (follows.containsKey(userId)) {
            for (int followee : follows.get(userId)) {
                if (tweets.containsKey(followee)) {
                    maxHeap.addAll(tweets.get(followee));
                }
            }
        }

        // pick the 10 most recent
        List<Integer> feed = new ArrayList<>();
        int count = 0;
        while (!maxHeap.isEmpty() && count < 10) {
            feed.add(maxHeap.poll().id);
            count++;
        }
        return feed;
    }

    public void follow(int followerId, int followeeId) {
        if (followerId == followeeId) return; // cannot follow self
        follows.putIfAbsent(followerId, new HashSet<>());
        follows.get(followerId).add(followeeId);
    }

    public void unfollow(int followerId, int followeeId) {
        if (followerId == followeeId) return; // cannot unfollow self
        if (follows.containsKey(followerId)) {
            follows.get(followerId).remove(followeeId);
        }
    }
}

/**
 * Example usage:
 *
 * Twitter twitter = new Twitter();
 * twitter.postTweet(1, 5);                    // user 1 posts tweet 5
 * System.out.println(twitter.getNewsFeed(1)); // [5]
 *
 * twitter.follow(1, 2);                       // user 1 follows user 2
 * twitter.postTweet(2, 6);                    // user 2 posts tweet 6
 * System.out.println(twitter.getNewsFeed(1)); // [6, 5]
 *
 * twitter.unfollow(1, 2);                     // user 1 unfollows user 2
 * System.out.println(twitter.getNewsFeed(1)); // [5]
 */
