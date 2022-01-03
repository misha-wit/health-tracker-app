<template id="food-profile">
  <app-layout>
    <div v-if="noFoodFound">
      <p> We're sorry, we were not able to retrieve this food.</p>
      <p> View <a :href="'/foods'">all foods</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noFoodFound">
      <div class="card-header">
        <div class="row">
          <div class="col-6"> Food Profile </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Update"
                    class="btn btn-info btn-simple btn-link"
                    @click="updateFood()">
              <i class="far fa-save" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete"
                    class="btn btn-info btn-simple btn-link"
                    @click="deleteFood()">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body">
        <form id="addFoods">

          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" style="width: 120px;" id="input-food-id">Food ID</span>
            </div>
            <input type="number" class="form-control" v-model="food.id" description="id" readonly placeholder="Id"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" style="width: 120px;" id="input-foods-userId">User Id</span>
            </div>
            <input type="number" class="form-control" v-model="food.userId" name="userId" readonly placeholder="userId"/>
            <select name="userId" class="form-control" v-model="food.userId">
              <option v-for="user in users" :value="user.id">{{user.name}}</option>
            </select>
          </div>

          <!--<div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" style="width: 120px;" id="input-foods-userId">User Id</span>
            </div>
            <div class="input-group mb-3">
              <select v-model="formData.userId" name="userId" class="form-control" v-model="foods.userId">
                <option v-for="user in users" :value="user.id">{{user.name}}</option>
              </select>
              <input type="number" class="form-control" v-model="formData.userId" name="userId"  readonly/>
            </div>-->
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" style="width: 120px;" id="input-foods-foodname">FoodName</span>
              </div>
              <input type="text" class="form-control" v-model="food.foodname" name="foodname" list="FoodList" placeholder="FoodName"/>
              <datalist id="FoodList">
                <option value="Egg and Milk">
                <option value="Sausage and Potato">
                <option value="Chapathy and Curry">
                <option value="Rice">
              </datalist>
            </div>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" style="width: 120px;" id="input-foods-mealname">MealName</span>
              </div>
              <input type="text" class="form-control" v-model="food.mealname" name="mealname" list="MealList" placeholder="MealName"/>
              <datalist id="MealList">
                <option value="BreakFast">
                <option value="Lunch">
                <option value="Evening Snacks">
                <option value="Dinner">

              </datalist>
            </div>

            <div class="input-group-prepend ">
              <span class="input-group-text" style="width: 120px;" id="input-foods-calories">Calories</span>
            </div>
            <input type="number" class="form-control" v-model="food.calories" placeholder="Calories Burned" name="calories">
          </div>
        </form>
      </div>
      <div class="card-footer text-left">
        <!--<p  v-if="foods.length == 0"> No foods yet...</p>
        <p  v-if="foods.length > 0"> Foods so far...</p>-->
        <ul>
          <li v-for="food in foods">
            {{ food.foodname }} for {{ food.foodtime }} minutes
          </li>
        </ul>
      </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component("food-profile", {
  template: "#food-profile",
  data: () => ({
    food: null,
    noFoodFound: false,
    foods: [],
    users: [],
    formData: []
  }),
  created: function () {
    const foodId = this.$javalin.pathParams["food-id"];
    const url = `/api/foods/${foodId}`
    axios.get(url)
        .then(res => this.food = res.data)
        .catch(error => {
          console.log("No food found for id passed in the path parameter: " + error)
          this.noFoodFound = true
        })
    axios.get("/api/users")
        .then(res => this.users = res.data)
        .catch(() => alert("Error while fetching users"));
  },
  methods: {
    updateFood: function () {
      const foodId = this.$javalin.pathParams["food-id"];
      const url = `/api/foods/${foodId}`
      const timestamp = new Date().toISOString().slice(0, 30);
      axios.patch(url,
          {
            foodname: this.food.foodname,
            mealname: this.food.mealname,
            calories: this.food.calories,
            userId:this.food.userId,
            foodtime:timestamp
          })
          .then(response =>
              this.food.push(response.data),window.location.href = '/foods')
          .catch(error => {
            console.log(error)
          })
      alert("Food updated!")
    },
    deleteFood: function () {
      if (confirm("Do you really want to delete?")) {
        const foodId = this.$javalin.pathParams["food-id"];
        const url = `/api/foods/${foodId}`
        axios.delete(url)
            .then(response => {
              alert("Food deleted")
              //display the /foods endpoint
              window.location.href = '/foods';
            })
            .catch(function (error) {
              console.log(error)
            });
      }
    }
  }
});
</script>