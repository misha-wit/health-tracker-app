<template id="activity-overview">
  <app-layout>
    <!-- Card - for adding a new user -->
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">
            Activities
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
        <form id="addActivities">
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" style="width: 120px;" id="input-activities-userId">User Id</span>
            </div>
            <div class="input-group mb-3">
              <select v-model="formData.userId" name="userId" class="form-control" v-model="activities.userId">
                <option v-for="user in users" :value="user.id">{{user.name}}</option>
              </select>
              <input type="number" class="form-control" v-model="formData.userId" name="userId"  readonly/>
            </div>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" style="width: 120px;" id="input-activities-description">Description</span>
              </div>
              <input type="text" class="form-control" v-model="formData.description" name="description" list="ActivityList" placeholder="Description"/>
              <datalist id="ActivityList">
                <option value="Bicycle riding">
                <option value="Dancing">
                <option value="Hiking">
                <option value="Running">
                <option value="Skipping">
                <option value="Stair Training">
                <option value="Squat Jacks">
                <option value="Swimming">
                <option value="Walking">
                <option value="Weight Lifting">
              </datalist>
            </div>


            <div class="input-group mb-3">
              <div class="input-group-prepend ">
                <span class="input-group-text" style="width: 120px;" id="input-activities-duration">Duration</span>
              </div>
              <input type="number" class="form-control" v-model="formData.duration" placeholder="Duration" name="duration">
              &nbsp&nbsp
              <div class="input-group-prepend ">
                <span class="input-group-text" style="width: 120px;" id="input-activities-calories">Calories</span>
              </div>
              <input type="number" class="form-control" v-model="formData.calories" placeholder="Calories Burned" name="calories">
            </div>
        </form>
        <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link" @click="addActivities()">Add Activities</button>

      </div>

    </div>

    <!-- List Group - displays all the users -->
    <div class="list-group list-group-flush">
      <div class="list-group-item d-flex align-items-start"
           v-for="(activities,index) in activities" v-bind:key="index"><div class="mr-auto p-2">
        <span><a :href="`/activities/${activities.id}`"> Activity : {{ activities.description }}, User Id : ({{ activities.userId }})</a></span>
      </div>

        <div class="p2">
          <a :href="`/activities/${activities.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
          </a>
          <button rel="tooltip" title="Delete" class="btn btn-info btn-simple btn-link"
                  @click="deleteActivities(activities, index)">
            <i class="fas fa-trash" aria-hidden="true"></i>
          </button>
        </div>
      </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component("activity-overview", {
  template: "#activity-overview",
  data: () => ({
    activities: [],
    formData: [],
    hideForm :true,
    users: []
  }),
  created() {
    this.fetchActivities();
    axios.get("/api/users")
        .then(res => this.users = res.data)
        .catch(() => alert("Error while fetching users"));
  },
  methods: {
    fetchActivities: function () {
      axios.get("/api/activities")
          .then(res => this.activities = res.data)
          .catch(() => alert("Error while fetching activities"));
    },
    deleteActivities: function (activities, index) {
      if (confirm('Are you sure you want to delete this activities? This action cannot be undone.', 'Warning')) {
        //user confirmed delete
        const activitiesId = activities.id;
        const url = `/api/activities/${activitiesId}`;
        axios.delete(url)
            .then(response =>
                //delete from the local state so Vue will reload list automatically
                this.activities.splice(index, 1).push(response.data))
            .catch(function (error) {
              console.log(error)
            });
      }
    },
    addActivities: function (){
      const url = `/api/activities`;
      const timestamp = new Date().toISOString().slice(0, 30);
      axios.post(url,
          {
            description: this.formData.description,
            duration: this.formData.duration,
            calories: this.formData.calories,
            userId:this.formData.userId,
            started:timestamp
          })
          .then(response => {
            this.activities.push(response.data)
            this.hideForm= true;
            this.fetchActivities()
          })
          .catch(error => {
            console.log(error)
          })
    }
  }
});
</script>