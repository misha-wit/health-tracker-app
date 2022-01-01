<template id="food-overview">
  <app-layout>
    <!-- Card - for adding a new user -->
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">
            Foods
          </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Add"
                    class="btn btn-info btn-simple btn-link"
                    @click="hideForm =!hideForm">
              <i class="fa fa-plus" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body" :class="{ 'd-none': hideForm}">
        <form id="addFoods">
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" style="width: 120px;" id="input-foods-userId">User Id</span>
            </div>
            <div class="input-group mb-3">
              <select v-model="formData.userId" name="userId" class="form-control" v-model="foods.userId">
                <option v-for="user in users" :value="user.id">{{user.name}}</option>
              </select>
              <input type="number" class="form-control" v-model="formData.userId" name="userId"  readonly/>
            </div>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" style="width: 120px;" id="input-foods-foodname">FoodName</span>
              </div>
              <input type="text" class="form-control" v-model="formData.foodname" name="foodname" list="FoodList" placeholder="FoodName"/>
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
              <input type="text" class="form-control" v-model="formData.mealname" name="mealname" list="MealList" placeholder="MealName"/>
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
              <input type="number" class="form-control" v-model="formData.calories" placeholder="Calories Burned" name="calories">
            </div>
        </form>
        <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link" @click="addFoods()">Add Foods</button>

      </div>

    </div>

    <!-- List Group - displays all the users -->
    <div class="list-group list-group-flush">
      <div class="list-group-item d-flex align-items-start"
           v-for="(foods,index) in foods" v-bind:key="index"><div class="mr-auto p-2">
        <span><a :href="`/foods/${foods.id}`"> Food : {{ foods.foodname }} User Id : ({{ foods.userId }})</a></span>
      </div>

        <div class="p2">
          <a :href="`/foods/${foods.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
          </a>
          <button rel="tooltip" title="Delete" class="btn btn-info btn-simple btn-link"
                  @click="deleteFoods(foods, index)">
            <i class="fas fa-trash" aria-hidden="true"></i>
          </button>
        </div>
      </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component("food-overview", {
  template: "#food-overview",
  data: () => ({
    foods: [],
    formData: [],
    hideForm :true,
    users: []
  }),
  created() {
    this.fetchFoods();
    axios.get("/api/users")
        .then(res => this.users = res.data)
        .catch(() => alert("Error while fetching users"));
  },
  methods: {
    fetchFoods: function () {
      axios.get("/api/foods")
          .then(res => this.foods = res.data)
          .catch(() => alert("Error while fetching foods"));
    },
    deleteFoods: function (foods, index) {
      if (confirm('Are you sure you want to delete this foods? This action cannot be undone.', 'Warning')) {
        //user confirmed delete
        const foodsId = foods.id;
        const url = `/api/foods/${foodsId}`;
        axios.delete(url)
            .then(response =>
                //delete from the local state so Vue will reload list automatically
                this.foods.splice(index, 1).push(response.data))
            .catch(function (error) {
              console.log(error)
            });
      }
    },
    addFoods: function (){
      const url = `/api/foods`;
      const timestamp = new Date().toISOString().slice(0, 30);
      axios.post(url,
          {
            foodname: this.formData.foodname,
            mealname: this.formData.mealname,
            calories: this.formData.calories,
            userId:this.formData.userId,
            foodtime:timestamp
          })
          .then(response => {
            this.foods.push(response.data)
            this.hideForm= true;
          })
          .catch(error => {
            console.log(error)
          })
    }
  }
});
</script>