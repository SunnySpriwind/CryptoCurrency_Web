<template>
  <div class="container">
    <div class="chart-area">
      <div class="panel">
        <label class="form-label">Coin:</label>
        <el-autocomplete
            class="input"
            v-model="coinname"
            :fetch-suggestions="querySearch"
            placeholder="Enter the name of coin"
        ></el-autocomplete>

        <label class="form-label">Market:</label>
        <el-select v-model="market" class="input" placeholder="Select market">
          <el-option label="CoinGecko" value="CoinGecko"></el-option>
          <el-option label="CoinMarketCap" value="CoinMarketCap"></el-option>
        </el-select>

        <el-button class="button" @click="searchCoinSeries">Search</el-button>
        <el-button class="button" @click="exportToExcel">Export</el-button>
      </div>

      <div class="chart-background">
        <div id="main" class="chart"></div>
      </div>

      <div class="change-chart">
        <el-button class="button" @click="toggleChart('price')">Price</el-button>
        <el-button class="button" @click="toggleChart('volumn')" style="width:100px">Volumn</el-button>
      </div>
    </div>

    <div class="vertical-line"></div>

    <div class="data-area">
      <h2 class="data-title">Data Points</h2>
      <div v-if="infoDataNow" class="data-content">
        <p><strong>Coin:</strong> {{ this.coinname }}</p>
        <p><strong>Market:</strong> {{ infoDataNow.market }}</p>
        <p>
          <strong>{{ currentChart === 'price' ? 'Price' : 'Volume' }}:</strong>
          {{ infoDataNow.price }}{{ currentChart === 'price' ? ' (USD)' : 'M' }}
        </p>
        <p><strong>Time:</strong> {{ infoDataNow.time }}</p>
      </div>

      <hr class="data-divider"/>

      <h2 class="data-title">24-Hour Summary</h2>
      <div v-if="infoData" class="data-content">
        <p><strong>Coin:</strong> {{ this.infoData[0].name }}</p>
        <p><strong>Market:</strong> {{ this.infoData[0].market }}</p>
        <p><strong>Max Price:</strong> {{ this.infoData[0].maxprice }} (USD) at {{ this.infoData[0].maxpricetime }}</p>
        <p><strong>Min Price:</strong> {{ this.infoData[0].minprice }} (USD) at {{ this.infoData[0].minpricetime }}</p>
      </div>
    </div>
  </div>
</template>

<style>

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 80px;
  padding: 0 20px;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
}

.container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  width: 100%;
  margin-top: 20px;
}

.chart-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 20px;
}

.panel {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 800px;
  max-width: 900px;
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-label {
  font-size: 16px;
  font-weight: bold;
  margin-right: 10px;
  white-space: nowrap;
}

.input {
  width: 220px;
  margin-right: 20px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.input:focus {
  border-color: #40a9ff;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1), 0 0 8px rgba(64, 169, 255, 0.6);
  outline: none;
}

.button {
  width: 100px;
  margin-left: 10px;
  padding: 10px 20px;
  background-color: #1890ff;
  color: #fff;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
}

.button:hover {
  background-color: #40a9ff;
}

.chart-background {
  background-color: #f3efef;
  width: 900px;
  height: 500px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart {
  width: 100%;
  height: 100%;
}

.vertical-line {
  width: 2px;
  background-color: #000;
  height: 620px;
  margin-top: 0px;
}

.data-area {
  margin-top: 60px;
  width: 300px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.data-title {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 20px;
  text-transform: uppercase;
}

.data-content p {
  margin-bottom: 10px;
  font-size: 16px;
  color: #666;
}

.data-divider {
  border-top: 1px solid #d9d9d9;
  margin: 20px 0;
}

.change-chart {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>

<script>
import * as echarts from 'echarts';
import request from '@/utils/request';
import * as XLSX from 'xlsx';

export default {
  name: 'RealTimeView',

  data() {
    return {
      coinname: '',
      market: '',
      availableCoins: ['bitcoin', 'ethereum', 'binancecoin'],
      chart: null,
      tooltipData: null,
      infoData: null,
      infoDataNow: null,
      updateInterval: 60000,
      intervalId: null,
      currentChart: 'price',
      // Chart
      option: {
        title: {
          text: 'Real-Time Price Every one min',
          left: '50%',
          textAlign: 'center'
        },

        // Chart's points
        tooltip: {
          trigger: 'axis',
          formatter: function (params) {
            if (params.length > 0) {
              let date = new Date(params[0].value[0]);
              let hours = date.getHours().toString().padStart(2, '0');
              let minutes = date.getMinutes().toString().padStart(2, '0');
              let coinName = params[0].seriesName.replace(' Price', '');
              let market = params[0].value[2];
              market = market === '1' ? 'CoinGecko' : market === '3' ? 'CoinMarketCap' : market;
              return `Coin: ${coinName}<br/>Market: ${market}<br/>Price: ${params[0].value[1]}<br/>Time: ${hours}:${minutes}`;
            }
            return '';
          },

          axisPointer: {
            animation: false
          }
        },

        xAxis: {
          type: 'time',
          splitLine: {
            show: false
          },
          axisLabel: {
            formatter: function (value) {
              let date = new Date(value);
              let hours = date.getHours().toString().padStart(2, '0');
              let minutes = date.getMinutes().toString().padStart(2, '0');
              return `${hours}:${minutes}`;
            }
          },
          interval: 300000,
        },

        yAxis: {
          type: 'value',
          interval: 500,
          boundaryGap: [0, '100%'],
          splitLine: {
            show: true
          }
        },
        series: []
      }
    };
  },

  mounted() {
    // Initialize and load chart
    this.chart = echarts.init(document.getElementById('main'));
    this.chart.setOption(this.option);

    // Get the data of points that are in chart
    this.chart.on('showTip', this.handleTooltipChange);

  },

  beforeDestroy() {
    // Clear the interval when the component is destroyed
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  },

  methods: {

    querySearch(queryString, callback) {
      const results = queryString
          ? this.availableCoins.filter(coin => coin.toLowerCase().includes(queryString.toLowerCase()))
          : this.availableCoins;
      callback(results.map(coin => ({value: coin})));
    },

    searchCoinSeries() {
      // To clear existing chart.
      this.option.series = [];
      this.chart.clear();

      // To reset the x, yAxis and points

      this.option.xAxis = {
        type: 'time',
        splitLine: {
          show: false
        },
        axisLabel: {
          formatter: function (value) {
            let date = new Date(value);
            let hours = date.getHours().toString().padStart(2, '0');
            let minutes = date.getMinutes().toString().padStart(2, '0');
            return `${hours}:${minutes}`;
          }
        },
        interval: 300000,
      }

      this.option.yAxis = {
        type: 'value',
        interval: 500,
        boundaryGap: [0, '100%'],
        splitLine: {
          show: true
        }
      }

      this.option.tooltip = {
        trigger: 'axis',
        formatter: function (params) {
          if (params.length > 0) {
            let date = new Date(params[0].value[0]);
            let hours = date.getHours().toString().padStart(2, '0');
            let minutes = date.getMinutes().toString().padStart(2, '0');
            let coinName = params[0].seriesName.replace(' Price', '');
            let market = params[0].value[2];
            market = market === '1' ? 'CoinGecko' : market === '3' ? 'CoinMarketCap' : market;
            return `Coin: ${coinName}<br/>Market: ${market}<br/>Price: ${params[0].value[1]}<br/>Time: ${hours}:${minutes}`;
          }
          return '';
        }
      }

      // To check the input
      if (this.coinname.trim() === '' || this.market.trim() === '') {
        alert('Please enter a valid coin name or market.');
        return;
      }

      // To get static Data
      this.getStaticData(this.coinname, this.market);

      request.get('/realtime/general', {params: {name: this.coinname, market: this.market}})
          .then(response => {
            if (response.code === 1) {
              this.processData(response.data, `${this.coinname} Price`);
              // To update data immediately after search
              this.updateData(this.coinname);

              if (!this.intervalId) {
                this.toggleChart(this.currentChart); // Start the timer
              }
            } else {
              console.error('Error Searching data:', response.message);
            }
          })
          .catch(error => {
            console.error('Error Searching data:', error);
          });
    },

    processData(data, seriesName) {

      // To select data that are in the range of 2 hours.
      let now = new Date();
      let twentyFourHoursAgo = new Date(now - 2 * 60 * 60 * 1000);

      data = data.filter(item => new Date(item.pricetime) >= twentyFourHoursAgo);

      // To sort which Date is before
      data.sort((a, b) => new Date(a.pricetime) - new Date(b.pricetime));

      // To filter the points that are different price.
      let filteredData = [];
      let lastPrice = null;
      data.forEach(item => {
        let currentPrice = parseFloat(item.price);
        if (currentPrice !== lastPrice) {
          filteredData.push(item);
          lastPrice = currentPrice;
        }
      });

      let formattedData = filteredData.map(item => ({
        name: new Date(item.pricetime).getTime(),
        value: [
          new Date(item.pricetime).getTime(),
          parseFloat(item.price),
          item.market
        ]
      }));

      // To set the range of y-axis
      let prices = formattedData.map(item => parseFloat(item.value[1]));
      let allPrices = this.option.series.reduce((acc, series) => {
        return acc.concat(series.data.map(item => item.value[1]));
      }, []);
      allPrices = allPrices.concat(prices);
      let globalMinPrice = Math.floor(Math.min(...allPrices) / 500) * 500; // round down to nearest 500
      let globalMaxPrice = Math.ceil(Math.max(...allPrices) / 500) * 500; // round up to nearest 500
      this.option.yAxis.min = globalMinPrice;
      this.option.yAxis.max = globalMaxPrice;

      // To draw the chart
      let newSeries = {
        name: seriesName,
        type: 'line',
        showSymbol: true,
        symbolSize: 2,
        data: formattedData,
        large: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {offset: 0, color: 'rgb(123,104,238)'},
            {offset: 1, color: 'rgba(255, 99, 71, 0)'}
          ])
        },
        lineStyle: {
          color: 'rgba(255, 99, 71, 1)',
          width: 2
        }
      };

      // To add new line into Series
      this.option.series.push(newSeries);

      // To show line
      this.chart.setOption(this.option);
    },

    updateData() {
      this.option.series.forEach(series => {
        let coinName = series.name.replace(' Price', '');
        request.get('/realtime/general', {params: {name: coinName, market: this.market}})
            .then(response => {
              if (response.code === 1) {
                this.processData(response.data, series.name);
              } else {
                console.error('Error fetching data:', response.message);
              }
            })
            .catch(error => {
              console.error('Error fetching data:', error);
            });
      });
    },

    getStaticData(name, market) {
      this.infoData = [];

      // Initialize infoData with an object
      let infoDataObj = {
        id: 0,
        name: name,
        maxprice: null,
        minprice: null,
        market: market,
        maxpricetime: null,
        minpricetime: null,
        vol: null
      };

      this.infoData.push(infoDataObj);

      request.get('/realtime/maxprice', {params: {name: name, market: market}})
          .then(response => {
            if (response.code === 1) {
              let {price, pricetime} = response.data;
              this.infoData[0].maxprice = price;  // Use index to update the first object's property
              this.infoData[0].maxpricetime = pricetime;
            } else {
              console.error('Error Static data:', response.message);
            }
          })
          .catch(error => {
            console.error('Error Static data:', error);
          });

      request.get('/realtime/minprice', {params: {name: name, market: market}})
          .then(response => {
            if (response.code === 1) {
              let {price, pricetime} = response.data;
              this.infoData[0].minprice = price;  // Use index to update the first object's property
              this.infoData[0].minpricetime = pricetime;
            } else {
              console.error('Error Static data:', response.message);
            }
          })
          .catch(error => {
            console.error('Error Static data:', error);
          });
    },

    updateVolumeChart() {
      this.option.series = [];
      this.chart.clear();
      this.option.yAxis = [{
        type: 'value',
        name: 'Volume',
        position: 'left',
        boundaryGap: [0, '50%'],
        splitLine: {
          show: true
        },
        axisLabel: {
          formatter: function (value) {
            return (value / 1000000).toFixed(2) + 'M';
          }
        }
      }];

      const coinname = this.coinname.trim() !== '' ? this.coinname.trim() : '';
      const market = this.market.trim() !== '' ? this.market.trim() : '';

      if (coinname && market) {
        request.get('/realtime/general', {params: {name: coinname, market: market}})
            .then(response => {
              if (response.code === 1) {
                const data = response.data;

                const now = new Date();
                const twoHoursAgo = new Date(now - 2 * 60 * 60 * 1000);

                const volumeData = data.map(item => ({
                  pricetime: item.pricetime,
                  volume: parseFloat(item.vol),
                  market: item.market,
                  name: item.name
                })).filter(item => new Date(item.pricetime) >= twoHoursAgo);

                // To filter the points that are the different price.
                let filteredData = [];
                let lastVolume = null;
                volumeData.forEach(item => {
                  if (item.volume !== lastVolume) {
                    filteredData.push(item);
                    lastVolume = item.volume;
                  }
                });

                this.option.series = [
                  {
                    name: 'Volume',
                    type: 'bar',
                    data: filteredData.map(item => ({
                      name: new Date(item.pricetime).getTime(),
                      value: [
                        new Date(item.pricetime).getTime(),
                        item.volume,
                        item.market,
                        item.name
                      ]
                    })),
                    large: true,
                    itemStyle: {
                      color: 'rgba(54, 162, 235, 0.8)'
                    }
                  },
                  {
                    name: 'Volume Line',
                    type: 'line',
                    data: filteredData.map(item => ({
                      name: new Date(item.pricetime).getTime(),
                      value: [
                        new Date(item.pricetime).getTime(),
                        item.volume,
                        item.market,
                        item.name
                      ]
                    })),
                    large: true,
                    lineStyle: {
                      color: 'rgba(255, 99, 71, 1)',
                      width: 2
                    },
                    symbol: 'circle',
                    symbolSize: 6,
                    itemStyle: {
                      color: 'rgba(255, 99, 71, 1)'
                    }
                  }
                ];

                this.option.xAxis = {
                  type: 'time',
                  splitLine: {
                    show: false
                  },
                  axisLabel: {
                    formatter: function (value) {
                      let date = new Date(value);
                      let hours = date.getHours().toString().padStart(2, '0');
                      let minutes = date.getMinutes().toString().padStart(2, '0');
                      return `${hours}:${minutes}`;
                    }
                  },
                  boundaryGap: false,
                  min: twoHoursAgo.getTime(),
                  max: now.getTime()
                };


                let volumes = filteredData.map(item => item.volume);
                if (volumes.length > 0) {
                  let minVolume = Math.min(...volumes);
                  let maxVolume = Math.max(...volumes);
                  this.option.yAxis[0].min = Math.floor(minVolume / 500) * 500;
                  this.option.yAxis[0].max = Math.ceil(maxVolume / 500) * 500;
                } else {
                  this.option.yAxis[0].min = null;
                  this.option.yAxis[0].max = null;
                }

                this.option.tooltip = {
                  trigger: 'axis',
                  formatter: function (params) {
                    if (params.length > 0) {
                      let date = new Date(params[0].value[0]);
                      let hours = date.getHours().toString().padStart(2, '0');
                      let minutes = date.getMinutes().toString().padStart(2, '0');
                      let coinName = params[0].value[3];
                      let market = params[0].value[2];
                      market = market === '1' ? 'CoinGecko' : market === '3' ? 'CoinMarketCap' : market;
                      return `Coin: ${coinName}<br/>Market: ${market}<br/>Volume: ${params[0].value[1]}<br/>Time: ${hours}:${minutes}`;
                    }
                    return '';
                  },
                  axisPointer: {
                    animation: false
                  }
                };

                this.chart.setOption(this.option);
              } else {
                console.error('Error fetching data:', response.message);
              }
            })
            .catch(error => {
              console.error('Error fetching data:', error);
            });
      }
    },

    toggleChart(chartType) {

      this.currentChart = chartType;

      if (this.intervalId) {
        clearInterval(this.intervalId);
      }

      if (this.currentChart === 'price') {
        this.intervalId = setInterval(() => {
          this.searchCoinSeries();
        }, this.updateInterval);
      } else if (this.currentChart === 'volumn') {
        this.intervalId = setInterval(() => {
          this.updateVolumeChart();
        }, this.updateInterval);
      }
      this.update();
    },

    update() {

      if (this.currentChart === 'price') {
        this.searchCoinSeries();
      } else if (this.currentChart === 'volumn') {
        this.updateVolumeChart();
      }
    },

    handleTooltipChange(params) {
      if (params.dataIndex !== undefined) {
        const dataIndex = params.dataIndex;
        const series = this.chart.getModel().getSeriesByIndex(params.seriesIndex);
        const data = series.getData().getItemModel(dataIndex).option;

        let market = data.value[2];
        market = market === '1' ? 'CoinGecko' : market === '3' ? 'CoinMarketCap' : market;

        this.infoDataNow = {
          name: series.name.replace(' Price', ''),
          market: market,
          price: data.value[1],
          time: new Date(data.value[0]).toLocaleString()
        };
      } else {
        console.warn('No data found in params:', params);
      }
    },

    exportToExcel() {

      if (!this.chart) return;


      const seriesData = this.option.series[0].data.map(item => {
        return {
          Time: new Date(item.value[0]).toLocaleString(),
          Value: item.value[1],
          Market: item.value[2],
          Coin: this.coinname
        };
      });


      const worksheet = XLSX.utils.json_to_sheet(seriesData);


      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, 'Chart Data');


      XLSX.writeFile(workbook, `${this.coinname}_${this.currentChart}_data.xlsx`);
    },

  },
};
</script>




