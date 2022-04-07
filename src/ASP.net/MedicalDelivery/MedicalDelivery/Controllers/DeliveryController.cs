using AutoMapper;
using MedicalDelivery.Helpers;
using MedicalDelivery.Models;
using MedicalDelivery.Services;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Controllers
{
    [Route("api/delivery")]
    [ApiController]
    public class DeliveryController : ControllerBase
    {
        private IMapper mapper;
        private readonly LibraryContext context;
        private IDeliveryService deliveryService;
        public DeliveryController(LibraryContext context, IMapper mapper, IDeliveryService deliveryService)
        {
            this.context = context;
            this.mapper = mapper;
            this.deliveryService = deliveryService;
        }
        [HttpPost("{hospitalID}/{medicineID}")]
        public IActionResult createDelivery([FromBody] Delivery newDelivery, [FromRoute] int hospitalID, [FromRoute] int medicineID)
        {
            var delivery = mapper.Map<Delivery>(newDelivery);
            try
            {
                deliveryService.create(delivery, hospitalID, medicineID);
                return Ok(delivery);
            }
            catch (AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
        [HttpGet]
        public IActionResult getAll()
        {
            var deliveries = deliveryService.getAll();
            IList<JObject> jsonDeliveryList = new List<JObject>();
            foreach (var delivery in deliveries)
            {
                string _json = JsonConvert.SerializeObject(delivery, Formatting.Indented, new JsonSerializerSettings
                {
                    ReferenceLoopHandling = ReferenceLoopHandling.Ignore
                });
                JObject jsonDelivery = JObject.Parse(_json);
                jsonDeliveryList.Add(jsonDelivery);
            }
            return Ok(jsonDeliveryList);
        }
        [HttpGet("{ID}")]
        public IActionResult getByID(int ID)
        {
            var delivery = deliveryService.getByID(ID);
            string _json = JsonConvert.SerializeObject(delivery, Formatting.Indented, new JsonSerializerSettings
            {
                ReferenceLoopHandling = ReferenceLoopHandling.Ignore
            });
            JObject jsonDelivery = JObject.Parse(_json);

            return Ok(jsonDelivery);
        }
        [HttpGet("getbyhospital/{ID}")]
        public IActionResult getByHospitalID(int ID)
        {
            var deliveries = deliveryService.getByHospitalID(ID);
            IList<JObject> jsonDeliveryList = new List<JObject>();
            foreach (var delivery in deliveries)
            {
                string _json = JsonConvert.SerializeObject(delivery, Formatting.Indented, new JsonSerializerSettings
                {
                    ReferenceLoopHandling = ReferenceLoopHandling.Ignore
                });
                JObject jsonDelivery = JObject.Parse(_json);
                jsonDeliveryList.Add(jsonDelivery);
            }
            return Ok(jsonDeliveryList);
        }
        [HttpGet("getbyrider/{ID}")]
        public IActionResult getByRiderID(int ID)
        {
            var deliveries = deliveryService.getByRiderID(ID);
            IList<JObject> jsonDeliveryList = new List<JObject>();
            foreach (var delivery in deliveries)
            {
                string _json = JsonConvert.SerializeObject(delivery, Formatting.Indented, new JsonSerializerSettings
                {
                    ReferenceLoopHandling = ReferenceLoopHandling.Ignore
                });
                JObject jsonDelivery = JObject.Parse(_json);
                jsonDeliveryList.Add(jsonDelivery);
            }
            return Ok(jsonDeliveryList);
        }
        [HttpGet("getnewdeliveries")]
        public IActionResult getNewDeliveries()
        {
            var deliveries = deliveryService.getNewDeliveries();
            IList<JObject> jsonDeliveryList = new List<JObject>();
            foreach (var delivery in deliveries)
            {
                string _json = JsonConvert.SerializeObject(delivery, Formatting.Indented, new JsonSerializerSettings
                {
                    ReferenceLoopHandling = ReferenceLoopHandling.Ignore
                });
                JObject jsonDelivery = JObject.Parse(_json);
                jsonDeliveryList.Add(jsonDelivery);
            }
            return Ok(jsonDeliveryList);
        }
        [HttpPut("notification/doctor/{ID}")]
        public IActionResult readNotificationDoctor([FromRoute] int ID) {
            try
            {
                var delivery = deliveryService.readNotificationDoctor(ID);
                return Ok(delivery);
            }
            catch (AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
        [HttpPut("notification/rider/{ID}")]
        public IActionResult readNotificationRider([FromRoute] int ID)
        {
            try
            {
                var delivery = deliveryService.readNotificationRider(ID);
                return Ok(delivery);
            }
            catch (AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
        [HttpPut("notification/response/{ID}")]
        public IActionResult responseUpdater([FromRoute] int ID)
        {
            try
            {
                var delivery = deliveryService.response(ID);
                return Ok(delivery);
            }
            catch (AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
        [HttpPut("{ID}")]
        public IActionResult updateDelivery([FromBody] Delivery delivery, int ID)
        {
            try
            {
                var ddelivery = deliveryService.updateDelivery(delivery, ID);
                return Ok(ddelivery);
            }
            catch (AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
    }
}
