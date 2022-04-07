using AutoMapper;
using MedicalDelivery.Helpers;
using MedicalDelivery.Models;
using MedicalDelivery.Services;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Controllers
{
    [Route("api/hospital")]
    [ApiController]
    public class HospitalController : ControllerBase
    {
        private IMapper mapper;
        private readonly LibraryContext context;
        private IHospitalService hospitalService;
        public HospitalController(LibraryContext context, IMapper mapper, IHospitalService hospitalService)
        {
            this.context = context;
            this.mapper = mapper;
            this.hospitalService = hospitalService;
        }
        [HttpPost]
        public IActionResult createHospital([FromBody] Hospital newHospital)
        {
            var hospital = mapper.Map<Hospital>(newHospital);
            try
            {
                hospitalService.create(hospital);
                return Ok(hospital);
            }
            catch(AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
        [HttpGet]
        public IActionResult getAll()
        {
            var hospitals = hospitalService.getAll();
            IList<JObject> jsonHospitalList = new List<JObject>();
            foreach (var hospital in hospitals)
            {
                string _json = JsonConvert.SerializeObject(hospital, Formatting.Indented, new JsonSerializerSettings
                {
                    ReferenceLoopHandling = ReferenceLoopHandling.Ignore
                });
                JObject jsonHospital = JObject.Parse(_json);
                jsonHospitalList.Add(jsonHospital);
            }
            return Ok(jsonHospitalList);
        }
        [HttpGet("{ID}")]
        public IActionResult getByID(int ID)
        {
            var hospital = hospitalService.getByID(ID);
            string _json = JsonConvert.SerializeObject(hospital, Formatting.Indented, new JsonSerializerSettings
            {
                ReferenceLoopHandling = ReferenceLoopHandling.Ignore
            });
            JObject jsonHospital = JObject.Parse(_json);

            return Ok(jsonHospital);
        }
        [HttpPut("{ID}")]
        public IActionResult updateHospital([FromBody] Hospital hospital, int ID)
        {
            try
            {
                hospitalService.update(hospital, ID);
                return Ok(hospital);
            }
            catch (AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }

        }
    }
}
