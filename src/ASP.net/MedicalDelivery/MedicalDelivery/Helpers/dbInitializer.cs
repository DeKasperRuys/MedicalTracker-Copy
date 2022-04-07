using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Helpers
{
    public class dbInitializer
    {
        public static void Initialize(LibraryContext context)
        {
            context.Database.EnsureCreated();
            context.SaveChanges();
        }
    }
}
