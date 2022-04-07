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
    [Route("api/medicine")]
    [ApiController]
    public class MedicineController : ControllerBase
    {
        private IMapper mapper;
        private readonly LibraryContext context;
        private IMedicineService medicineService;
        public MedicineController(LibraryContext context, IMapper mapper, IMedicineService medicineService)
        {
            this.context = context;
            this.mapper = mapper;
            this.medicineService = medicineService;
        }
        [HttpPost]
        public  IActionResult createMedicine([FromBody] Medicine newMedicine)
        {
            var medicine = mapper.Map<Medicine>(newMedicine);
            try
            {
                medicineService.create(medicine);
                return Ok(medicine);
            }
            catch(AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
        [HttpGet]
        public IActionResult getAll()
        {
            var medicines = medicineService.getAll();
            //Put all companies in a JSON var
            IList<JObject> jsonMedicineList = new List<JObject>();
            foreach (var medicine in medicines)
            {
                string _json = JsonConvert.SerializeObject(medicine, Formatting.Indented, new JsonSerializerSettings
                {
                    ReferenceLoopHandling = ReferenceLoopHandling.Ignore
                });
                JObject jsonMedicine = JObject.Parse(_json);
                jsonMedicineList.Add(jsonMedicine);
            }
            return Ok(jsonMedicineList);
        }
        [HttpGet("{ID}")]
        public IActionResult getByID(int ID)
        {
            var medicine = medicineService.getByID(ID);
            string _json = JsonConvert.SerializeObject(medicine, Formatting.Indented, new JsonSerializerSettings
            {
                ReferenceLoopHandling = ReferenceLoopHandling.Ignore
            });
            JObject jsonMedicine = JObject.Parse(_json);

            return Ok(jsonMedicine);
        }
        [HttpPut("{ID}")]
        public IActionResult updateMedicine([FromBody] Medicine medicine, int ID)
        {
            try
            {
                medicineService.update(medicine, ID);
                return Ok(medicine);
            }
            catch (AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }

        }
    }
}
