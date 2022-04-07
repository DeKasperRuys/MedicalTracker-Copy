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
    [Route("api/rider")]
    [ApiController]
    public class RiderController : ControllerBase
    {
        private IMapper mapper;
        private readonly LibraryContext context;
        private IRiderService riderService;
        public RiderController(LibraryContext context, IMapper mapper, IRiderService riderService)
        {
            this.context = context;
            this.mapper = mapper;
            this.riderService = riderService;
        }
        [HttpPost]
        public IActionResult createRider([FromBody] Rider newRider)
        {
            var rider = mapper.Map<Rider>(newRider);
            try
            {
                riderService.create(rider);
                return Ok(rider);
            }
            catch (AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
        [HttpGet]
        public IActionResult getAll()
        {
            var riders = riderService.getAll();
            IList<JObject> jsonRiderList = new List<JObject>();
            foreach (var rider in riders)
            {
                string _json = JsonConvert.SerializeObject(rider, Formatting.Indented, new JsonSerializerSettings
                {
                    ReferenceLoopHandling = ReferenceLoopHandling.Ignore
                });
                JObject jsonRider = JObject.Parse(_json);
                jsonRiderList.Add(jsonRider);
            }
            return Ok(jsonRiderList);
        }
        [HttpGet("{ID}")]
        public IActionResult getByID(int ID)
        {
            var rider = riderService.getByID(ID);
            string _json = JsonConvert.SerializeObject(rider, Formatting.Indented, new JsonSerializerSettings
            {
                ReferenceLoopHandling = ReferenceLoopHandling.Ignore
            });
            JObject jsonRider = JObject.Parse(_json);

            return Ok(jsonRider);
        }
    }
}