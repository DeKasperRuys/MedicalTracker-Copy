using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Models.Payload
{
    public class Payload
    {
        public Payload_Fields payload_fields { get; set; }
        public Metadata metadata { get; set; }
    }
}
