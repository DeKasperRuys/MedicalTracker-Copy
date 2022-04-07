using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Models.Payload
{
    public class Metadata
    {
        public String time { get; set; }
        public Gateways[] gateways { get; set; }
    }
}
