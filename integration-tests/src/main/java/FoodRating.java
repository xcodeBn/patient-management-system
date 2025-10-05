import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


class FoodRatings {

    // food -> cuisine
    private final Map<String, String> foodToCuisine;
    // food -> rating
    private final Map<String, Integer> foodToRating;
    // cuisine -> max heap of foods (by rating desc, name asc)
    private final Map<String, PriorityQueue<Food>> cuisineToFoods;


    static class Food{
        private String name;
        private int rating;

        public Food(String name, int rating) {
            this.name = name;
            this.rating = rating;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }
    }



    public FoodRatings(String[] foods, String[] cuisines, int[] ratings) {
        foodToCuisine = new HashMap<>();
        foodToRating = new HashMap<>();
        cuisineToFoods = new HashMap<>();

        for(int i = 0 ; i< foods.length; i++){
            String food = foods[i];
            int rating = ratings[i];
            String cuisine = cuisines[i];

            foodToCuisine.put(food, cuisine);
            foodToRating.put(food, rating);
            cuisineToFoods.computeIfAbsent(cuisine, k -> new PriorityQueue<>(
                    (a,b)-> {
                        if(a.rating!=b.rating){
                            return b.rating-a.rating;
                        }
                        return a.name.compareTo(b.name);
                    }
            )).add(new Food(food,rating));

        }
    }

    public void changeRating(String food, int newRating) {
        foodToRating.put(food, newRating);
    }

    public String highestRated(String cuisine) {
        PriorityQueue<Food> foods = cuisineToFoods.get(cuisine);
        while (true){
            Food top = foods.peek();


            if(top.rating == foodToRating.get(top.name)){
                return top.name;
            }
            foods.poll();
        }
    }
}

/**
 * Your FoodRatings object will be instantiated and called as such:
 * FoodRatings obj = new FoodRatings(foods, cuisines, ratings);
 * obj.changeRating(food,newRating);
 * String param_2 = obj.highestRated(cuisine);
 */