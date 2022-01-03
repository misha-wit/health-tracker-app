<template id="analysis-overview">
  <app-layout>
    <!-- Card - for adding a new user -->
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6"> Analysis
          </div>
        </div>
      </div>
      <div class="card-body" >
        <span class="input-group-text" style="width: 120px;" id="input-activities-userId">User Id</span>
        <select v-model="formData.userId" name="userId" class="form-control" v-model="activities.userId">
          <option v-for="user in users" :value="user.id">{{user.name}}</option>
        </select>
        <button rel="tooltip" title="Analysis" class="btn btn-info btn-simple btn-link" @click="viewAnalysis()">Analysis</button>
        <canvas id="analysisCanvas" width="400" height="100"></canvas>
      </div>

    </div>

    <!-- List Group - displays all the users -->
    <div class="list-group list-group-flush">

    </div>
  </app-layout>
</template>

<script>
Vue.component('analysis-overview',
    {
      template: "#analysis-overview",
      data: () => ({
        users: [],
        formData: [],
        resp: [],
        showpercChart:false
      }),
      created() {
        axios.get("/api/users")
            .then(res => this.users = res.data)
            .catch(() => alert("Error while fetching users"));
      },
      methods: {
        viewAnalysis: function () {
          const userId = this.formData.userId;
          const url2 = `/api/analysis/measurement/${userId}`
          axios.get(url2)
              .then(res => this.generateChart(res.data))
              .catch(() => alert("No Data to Fetch"));
        },
        generateChart:function(resp){
          this.resp = resp;
          this.analysisChart(resp);
          this.showpercChart = true;

        },
        analysisChart: function (resp) {

          const ctx = document.getElementById('analysisCanvas');

          const myChart = new Chart(ctx, {
            type: 'polarArea',
            data: {
              labels: ['calories','weight'],
              datasets: [{
                label: ['calories','weight'],
                data: [resp[0].calories,resp[0].weight],
                backgroundColor: [
                  'rgba(255, 99, 132, 0.2)',
                  'rgba(54, 162, 235, 0.2)',
                  'rgba(255, 206, 86, 0.2)',
                  'rgba(75, 192, 192, 0.2)',
                  'rgba(153, 102, 255, 0.2)',
                  'rgba(255, 159, 64, 0.2)'
                ],
                borderColor: [
                  'rgba(255, 99, 132, 1)',
                  'rgba(54, 162, 235, 1)',
                  'rgba(255, 206, 86, 1)',
                  'rgba(75, 192, 192, 1)',
                  'rgba(153, 102, 255, 1)',
                  'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 1
              }]
            },
            options: {
              scales: {
                y: {
                  beginAtZero: true
                }
              }
            }
          });


          const vals = resp.length
          for (let i = 1; i < vals; i++) {
            myChart.data.datasets.push({
              label: ['calories','weight'],
              data: [resp[i].calories,resp[i].weight],
              backgroundColor: ['rgba(255, 99, 132, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(255, 206, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)'],
              borderColor: ['rgba(255, 99, 132, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',],
              barThickness:50,
              borderWidth: 1,
            });
          }
          myChart.update()


        }
      }
    });


</script>