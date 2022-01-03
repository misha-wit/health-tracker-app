<template id="activity-profile">
  <app-layout>
    <div v-if="noActivityFound">
      <p> We're sorry, we were not able to retrieve this activity.</p>
      <p> View <a :href="'/activities'">all activities</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noActivityFound">
      <div class="card-header">
        <div class="row">
          <div class="col-6"> Activity Profile </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Update"
                    class="btn btn-info btn-simple btn-link"
                    @click="updateActivity()">
              <i class="far fa-save" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete"
                    class="btn btn-info btn-simple btn-link"
                    @click="deleteActivity()">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body">
        <form id="addActivities">
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" style="width: 120px;" id="input-activity-id">Activity ID</span>
            </div>
            <input type="number" class="form-control" v-model="activity.id" description="id" readonly placeholder="Id"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" style="width: 120px;" id="input-activities-userId">User Id</span>
            </div>
            <input type="number" class="form-control" v-model="activity.userId" name="userId" readonly placeholder="userId"/>
            <select  name="userId" class="form-control" v-model="activity.userId">
              <option v-for="user in users" :value="user.id">{{user.name}}</option>
            </select>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" style="width: 120px;" id="input-activities-description">Description</span>
            </div>
            <input type="text" class="form-control" v-model="activity.description" name="description" list="ActivityList" placeholder="Description"/>
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
            <input type="number" class="form-control" v-model="activity.duration" placeholder="Duration" name="duration">
            &nbsp&nbsp
            <div class="input-group-prepend ">
              <span class="input-group-text" style="width: 120px;" id="input-activities-calories">Calories</span>
            </div>
            <input type="number" class="form-control" v-model="activity.calories" placeholder="Calories Burned" name="calories">
          </div>
        </form>
      </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component("activity-profile", {
  template: "#activity-profile",
  data: () => ({
    activity: null,
    noActivityFound: false,
    activities: [],
    users: [],
    formData: []
  }),
  created: function () {
    const activityId = this.$javalin.pathParams["activity-id"];
    const url = `/api/activities/${activityId}`
    axios.get(url)
        .then(res => this.activity = res.data)
        .catch(error => {
          console.log("No activity found for id passed in the path parameter: " + error)
          this.noActivityFound = true
        })
    axios.get("/api/users")
        .then(res => this.users = res.data)
        .catch(() => alert("Error while fetching users"));
  },
  methods: {
    updateActivity : function () {
    const activityid = this.$javalin.pathParams["activity-id"];
    const url = `/api/activities/${activityid}`
    const timestamp = new Date().toISOString().slice(0, 30);
      // alert(this.activity.description)
    axios.patch(url,
        {
          description: this.activity.description,
          duration: this.activity.duration,
          calories: this.activity.calories,
          userId: this.activity.userId,
          started:timestamp
        })
        .then(response =>
            this.activities.push(response.data),window.location.href = '/activities')

        .catch(error => {
          console.log(error)
        })
      alert("Activity updated!")
  },
    deleteActivity: function () {
      if (confirm('Are you sure you want to delete this activities? This action cannot be undone.', 'Warning')) {
        //activities confirmed delete
        const activitiesId = this.$javalin.pathParams["activity-id"];
        const url = `/api/activities/${activitiesId}`;
        axios.delete(url)
            .then(response =>
                //delete from the local state so Vue will reload list automatically
                this.activities.splice(index, 1).push(response.data))
            .catch(function (error) {
              console.log(error)
            });
      }
    }
  }
});
</script>