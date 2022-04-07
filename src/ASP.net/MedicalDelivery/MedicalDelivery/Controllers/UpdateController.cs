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
    [Route("api/update")]
    [ApiController]
    public class UpdateController : ControllerBase
    {
        private IMapper mapper;
        private readonly LibraryContext context;
        private IUpdateService updateService;
        public UpdateController(LibraryContext context, IMapper mapper, IUpdateService updateService)
        {
            this.context = context;
            this.mapper = mapper;
            this.updateService = updateService;
        }
        [HttpPost("{ID}")]
        public IActionResult createUpdate([FromBody] Update newUpdate, int ID)
        {
            var update = mapper.Map<Update>(newUpdate);
            try
            {
                updateService.create(update, ID);
                return Ok();
            }
            catch (AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
        [HttpGet]
        public IActionResult getAll()
        {
            var updates = updateService.getAll();
            IList<JObject> jsonUpdateList = new List<JObject>();
            foreach (var update in updates)
            {
                string _json = JsonConvert.SerializeObject(update, Formatting.Indented, new JsonSerializerSettings
                {
                    ReferenceLoopHandling = ReferenceLoopHandling.Ignore
                });
                JObject jsonUpdate = JObject.Parse(_json);
                jsonUpdateList.Add(jsonUpdate);
            }
            return Ok(jsonUpdateList);
        }
        [HttpGet("specific/{ID}")]
        public IActionResult getByID(int ID)
        {
            var update = updateService.getByID(ID);
            string _json = JsonConvert.SerializeObject(update, Formatting.Indented, new JsonSerializerSettings
            {
                ReferenceLoopHandling = ReferenceLoopHandling.Ignore
            });
            JObject jsonUpdate = JObject.Parse(_json);

            return Ok(jsonUpdate);
        }
        [HttpGet("{deliveryID}")]
        public IActionResult getByDeliveryID(int deliveryID)
        {
            var updates = updateService.getByDeliveryID(deliveryID);
            IList<JObject> jsonUpdateList = new List<JObject>();
            foreach (var update in updates)
            {
                string _json = JsonConvert.SerializeObject(update, Formatting.Indented, new JsonSerializerSettings
                {
                    ReferenceLoopHandling = ReferenceLoopHandling.Ignore
                });
                JObject jsonUpdate = JObject.Parse(_json);
                jsonUpdateList.Add(jsonUpdate);
            }
            return Ok(jsonUpdateList);
        }
    }
}
