
using AutoMapper;
using MedicalDelivery.Helpers;
using MedicalDelivery.Models;
using MedicalDelivery.Models.Payload;
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
    [Route("api/payload")]
    [ApiController]
    public class PayloadController : ControllerBase
    {
        private IMapper mapper;
        private readonly LibraryContext context;
        private IPayloadService payloadService;
        public PayloadController(LibraryContext context, IMapper mapper, IPayloadService payloadService)
        {
            this.context = context;
            this.mapper = mapper;
            this.payloadService = payloadService;
        }
        [Route("/api/payload/{ID}")]
        [HttpPost]
        public IActionResult createUpdate([FromBody] Payload newPayload, int ID)
        {
            var payload = mapper.Map<Payload>(newPayload);
            try
            {
                payloadService.create(payload, ID);
                return Ok(payload);
            }
            catch (AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
    }
}
